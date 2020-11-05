package com.example.go4lunch.models.restaurant

import com.example.go4lunch.models.restaurant.OpeningHours
import com.google.gson.annotations.SerializedName

data class PlaceDetail (
    @SerializedName("opening_hours") val openingHours : OpeningHours,
    @SerializedName("formatted_phone_number") val phoneNumber : String,
    @SerializedName("website") val website : String
)