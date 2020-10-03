package com.example.go4lunch.models

import com.google.gson.annotations.SerializedName

data class Open (

    @SerializedName("day") val day : Int,
    @SerializedName("time") val time : Int
)