package com.example.go4lunch.models

import com.google.gson.annotations.SerializedName

data class Places (

    @SerializedName("html_attributions") val html_attributions : List<String>,
    @SerializedName("results") val results : List<Place>,
    @SerializedName("status") val status : String
)