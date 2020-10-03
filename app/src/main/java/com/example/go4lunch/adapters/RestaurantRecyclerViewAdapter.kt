package com.example.go4lunch.adapters

import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.go4lunch.R
import com.example.go4lunch.models.Place
import com.example.go4lunch.models.PlaceDetail
import com.example.go4lunch.models.PlaceDetails
import com.example.go4lunch.utils.APIConstants
import com.example.go4lunch.utils.APIParse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_row_restaurant.view.*
import java.util.*

class RestaurantRecyclerViewAdapter : RecyclerView.Adapter<RestaurantRecyclerViewAdapter.RestaurantViewHolder> {

    var mPlaces: MutableList<Place>
    var mDetails: MutableList<PlaceDetails?>

    constructor(places: List<Place>, details: List<PlaceDetails?>) {
        mPlaces = places.toMutableList()
        mDetails = details.toMutableList()

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
        holder.updateWithRestaurant(mPlaces[position], mDetails[position]?.result)
    }

    private fun removeClosedRestaurants() {
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

    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun updateWithRestaurant(place: Place, details: PlaceDetail?) {
            itemView.list_row_restaurant_text_name.text = place.name
            itemView.list_row_restaurant_text_type_and_address.text = APIParse.parseAddress(place.vicinity)

            if (details != null) {
                if (details.openingHours != null) {
                    itemView.list_row_restaurant_text_opening_hours.text = APIParse.parseOpeningHours(details.openingHours.periods, Calendar.getInstance().get(Calendar.DAY_OF_WEEK))

                    if (itemView.list_row_restaurant_text_opening_hours.text == "Closing soon") {
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
        }
    }
}