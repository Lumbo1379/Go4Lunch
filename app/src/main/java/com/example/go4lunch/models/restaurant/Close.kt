package com.example.go4lunch.models.restaurant

import com.google.gson.annotations.SerializedName

data class Close (

    @SerializedName("day") val day : Int,
    @SerializedName("time") val time : Int
)