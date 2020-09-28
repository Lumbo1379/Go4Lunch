package com.example.go4lunch.models

import com.google.gson.annotations.SerializedName

data class OpeningHours (

    @SerializedName("open_now") val open_now : Boolean
)