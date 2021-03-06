package com.example.go4lunch.models.restaurant

import com.example.go4lunch.models.restaurant.PlaceDetail
import com.google.gson.annotations.SerializedName

data class PlaceDetails (
    @SerializedName("html_attributions") val htmAttributions : List<String>,
    @SerializedName("result") val result : PlaceDetail,
    @SerializedName("status") val status : String
)