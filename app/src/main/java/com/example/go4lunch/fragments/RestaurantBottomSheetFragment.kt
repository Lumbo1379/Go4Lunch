package com.example.go4lunch.fragments

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.go4lunch.R
import com.example.go4lunch.models.restaurant.Place
import com.example.go4lunch.utils.APIConstants
import com.example.go4lunch.utils.APIParse
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.bottom_sheet_restaurant.view.*

class RestaurantBottomSheetFragment : BottomSheetDialogFragment {

    private var mPlace: Place

    constructor(place: Place) {
        mPlace = place
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_restaurant, container, false)

        view.bottom_sheet_restaurant_image_rectangle.setBackgroundColor(view.resources.getColor(R.color.colorBottomSheet))
        view.bottom_sheet_restaurant_name.text = mPlace.name
        view.bottom_sheet_restaurant_address.text = APIParse.parseAddress(mPlace.vicinity)
        Picasso.get().load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + mPlace.photos[0].photo_reference + "&key=" + APIConstants.API_KEY).into(view.bottom_sheet_restaurant_image)

        val rating = mPlace.rating * 0.6 // Out of 5 to out of 3
        val width = (rating * 10).toFloat()  // 10dp for each star
        view.bottom_sheet_restaurant_image_rating.layoutParams.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, view.resources.displayMetrics).toInt() // Convert px to dp

        return view
    }
}