package com.example.go4lunch.models

import com.google.gson.annotations.SerializedName

data class OpeningHours (
    @SerializedName("open_now") val openNow : Boolean,
    @SerializedName("weekday_text") val weekdayText : List<String>,
    @SerializedName("periods") val periods : List<Period>
)