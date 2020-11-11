package com.example.go4lunch.adapters

import android.content.SharedPreferences
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.go4lunch.R
import com.example.go4lunch.fragments.RestaurantBottomSheetFragment
import com.example.go4lunch.helpers.UserHelper
import com.example.go4lunch.models.restaurant.Place
import com.example.go4lunch.models.restaurant.PlaceDetail
import com.example.go4lunch.models.restaurant.PlaceDetails
import com.example.go4lunch.utils.APIConstants
import com.example.go4lunch.utils.APIParse
import com.example.go4lunch.utils.Maths
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_row_restaurant.view.*
import java.util.*
import kotlin.math.roundToInt

class RestaurantRecyclerViewAdapter : RecyclerView.Adapter<RestaurantRecyclerViewAdapter.RestaurantViewHolder> {

    private var mPlaces: MutableList<Place>
    private var mDetails: MutableList<PlaceDetails?>
    private var mPreferences: SharedPreferences
    private var mActivity: FragmentActivity?

    constructor(places: List<Place>, details: List<PlaceDetails?>, preferences: SharedPreferences, activity: FragmentActivity?) {
        mPlaces = places.toMutableList()
        mDetails = details.toMutableList()
        mPreferences = preferences
        mActivity = activity

        removeClosedRestaurants()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.list_row_restaurant, parent, false) // Inflate single list item article view into recycler view

        return RestaurantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPlaces.size
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.updateWithRestaurant(mPlaces[position], mDetails[position]?.result, mPreferences)
        holder.itemView.tag = position

        holder.itemView.setOnClickListener { // Set up even to trigger swipeable restaurant details
            val position = it.tag.toString().toInt()

            val bottomSheet = RestaurantBottomSheetFragment(mPlaces[position], mDetails[position]?.result!!, mActivity!!.applicationContext)

            if (mActivity != null) {
                bottomSheet.show(mActivity!!.supportFragmentManager, "restaurantBottomSheet")
            }
        }
    }

    private fun removeClosedRestaurants() { // Don't show closed restaurants
        var i = 0

        while (i < mPlaces.size) {
            if (mDetails[i]?.result?.openingHours?.openNow == false || mDetails[i]?.result?.openingHours == null) {
                mDetails.removeAt(i)
                mPlaces.removeAt(i)
            } else {
                i++
            }
        }
    }

    fun clear() { // Reset view, use when switching languages
        val size = mPlaces.size
        mPlaces.clear()
        mDetails.clear()
        notifyDataSetChanged()
    }

    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { // Populate view
        fun updateWithRestaurant(place: Place, details: PlaceDetail?, preferences: SharedPreferences) {
            itemView.list_row_restaurant_text_name.text = place.name
            itemView.list_row_restaurant_text_address.text = APIParse.parseAddress(place.vicinity)

            if (details != null) {
                if (details.openingHours != null) {
                    itemView.list_row_restaurant_text_opening_hours.text = APIParse.parseOpeningHours(details.openingHours.periods, Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1, itemView.context) // Sunday is 1, we want it to be 0

                    if (itemView.list_row_restaurant_text_opening_hours.text == itemView.context.getString(R.string.closing_soon)) {
                        itemView.list_row_restaurant_text_opening_hours.setTextColor(Color.RED)
                    } else {
                        itemView.list_row_restaurant_text_opening_hours.setTextColor(itemView.resources.getColor(R.color.colorText))
                    }
                }
            }

            val rating = place.rating * 0.6 // Out of 5 to out of 3
            val width = (rating * 10).toFloat()  // 10dp for each star
            itemView.list_row_restaurant_image_rating.layoutParams.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, itemView.resources.displayMetrics).toInt() // Convert px to dp

            Picasso.get().load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + place.photos[0].photo_reference + "&key=" + APIConstants.API_KEY).into(itemView.list_row_restaurant_image_snapshot)

            itemView.list_row_restaurant_text_distance.text = Maths.latLngDistance(place.geometry.location.lat, place.geometry.location.lng, preferences).roundToInt().toString() + "m"

            UserHelper.getUsers(place.place_id).get().addOnSuccessListener {

                if (!it.isEmpty) {
                    itemView.list_row_restaurant_text_coworkers.visibility = View.VISIBLE
                    itemView.list_row_restaurant_image_coworkers.visibility = View.VISIBLE
                    itemView.list_row_restaurant_text_coworkers.text = "(" + it.size() + ")"
                } else {
                    itemView.list_row_restaurant_text_coworkers.visibility = View.INVISIBLE
                    itemView.list_row_restaurant_image_coworkers.visibility = View.INVISIBLE
                }

            }
        }
    }
}