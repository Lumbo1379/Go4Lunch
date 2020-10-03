package com.example.go4lunch.models

import com.google.gson.annotations.SerializedName

data class PlaceDetail (
    @SerializedName("opening_hours") val openingHours : OpeningHours
)