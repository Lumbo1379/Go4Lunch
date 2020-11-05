package com.example.go4lunch.fragments

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.go4lunch.R
import com.example.go4lunch.adapters.RestaurantRecyclerViewAdapter
import com.example.go4lunch.models.restaurant.Place
import com.example.go4lunch.models.restaurant.PlaceDetails
import com.example.go4lunch.models.restaurant.Places
import com.example.go4lunch.utils.APICalls
import com.example.go4lunch.utils.APIConstants
import com.example.go4lunch.utils.PreferenceKeys
import kotlinx.android.synthetic.main.fragment_list_view.*

class ListViewFragment : Fragment(), APICalls.ICallBacks {

    private lateinit var mPlaces: Places
    private var mPlacesDetails: MutableList<PlaceDetails?> = mutableListOf()
    private lateinit var mPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val preferences = activity?.getPreferences(Context.MODE_PRIVATE)
        if (preferences != null) {
            val location = Location("dummy")
            location.latitude = preferences.getFloat(PreferenceKeys.PREF_KEY_LAT, 0F).toDouble()
            location.longitude = preferences.getFloat(PreferenceKeys.PREF_KEY_LNG, 0F).toDouble()

            APICalls.fetchPlaces(this, location, 1500, "restaurant", "restaurant", APIConstants.API_KEY)

            mPreferences = preferences
        }

        return inflater.inflate(R.layout.fragment_list_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_list_recycler_view_restaurants.adapter = RestaurantRecyclerViewAdapter(emptyList(), emptyList(), mPreferences, activity)
    }

    override fun onResponse(places: Places?) {
        if (places != null) {
            mPlaces = places

            APICalls.fetchPlaceDetails(this, mPlaces.results[0].place_id, "opening_hours,formatted_phone_number,website", APIConstants.API_KEY)
        }
    }

    override fun onResponse(place: PlaceDetails?) {
            mPlacesDetails.add(place)

            if (mPlacesDetails.size == mPlaces.results.size) {
                updateRecyclerView(mPlaces.results, mPlacesDetails)
            } else {
                APICalls.fetchPlaceDetails(this, mPlaces.results[mPlacesDetails.size].place_id, "opening_hours,formatted_phone_number,website", APIConstants.API_KEY)
            }
    }

    override fun onFailure() {

    }

    private fun updateRecyclerView(places: List<Place>, details: List<PlaceDetails?>) {
        fragment_list_recycler_view_restaurants.layoutManager = LinearLayoutManager(activity)
        val adapter = RestaurantRecyclerViewAdapter(places, details, mPreferences, activity)
        fragment_list_recycler_view_restaurants.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}