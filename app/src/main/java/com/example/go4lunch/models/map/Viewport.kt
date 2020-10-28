package com.example.go4lunch.models.map

import com.example.go4lunch.models.map.Northeast
import com.example.go4lunch.models.map.Southwest
import com.google.gson.annotations.SerializedName

data class Viewport (

    @SerializedName("northeast") val northeast : Northeast,
    @SerializedName("southwest") val southwest : Southwest
)