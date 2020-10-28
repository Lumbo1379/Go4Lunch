package com.example.go4lunch.models.restaurant

import com.example.go4lunch.models.map.Close
import com.example.go4lunch.models.restaurant.Open
import com.google.gson.annotations.SerializedName

data class Period (

    @SerializedName("close") val close : Close,
    @SerializedName("open") val open : Open
)