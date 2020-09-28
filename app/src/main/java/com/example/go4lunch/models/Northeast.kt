package com.example.go4lunch.models

import com.google.gson.annotations.SerializedName

data class Northeast (

    @SerializedName("lat") val lat : Double,
    @SerializedName("lng") val lng : Double
)