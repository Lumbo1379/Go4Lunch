package com.example.go4lunch.models.map

import com.google.gson.annotations.SerializedName

data class Location (

    @SerializedName("lat") val lat : Double,
    @SerializedName("lng") val lng : Double
)