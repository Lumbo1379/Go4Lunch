package com.example.go4lunch.models

import com.google.gson.annotations.SerializedName

data class Place (

    @SerializedName("geometry") val geometry : Geometry,
    @SerializedName("name") val name : String,
    @SerializedName("photos") val photos : List<Photos>,
    @SerializedName("place_id") val place_id : String,
    @SerializedName("rating") val rating : Double,
    @SerializedName("vicinity") val vicinity : String
)