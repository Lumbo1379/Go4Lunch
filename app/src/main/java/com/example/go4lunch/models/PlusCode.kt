package com.example.go4lunch.models

import com.google.gson.annotations.SerializedName

data class PlusCode (

    @SerializedName("compound_code") val compound_code : String,
    @SerializedName("global_code") val global_code : String
)