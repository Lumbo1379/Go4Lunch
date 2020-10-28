package com.example.go4lunch.models.map

import com.google.gson.annotations.SerializedName

data class Geometry (

    @SerializedName("location") val location : Location,
    @SerializedName("viewport") val viewport : Viewport
)