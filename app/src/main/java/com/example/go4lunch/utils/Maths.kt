package com.example.go4lunch.utils

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import kotlin.math.*

class Maths {
    companion object {
        fun latLngDistance(lat1: Double, lng1: Double, preferences: SharedPreferences) : Double {
            val lat2 = preferences.getFloat(PreferenceKeys.PREF_KEY_LAT, 0F).toDouble()
            val lng2 = preferences.getFloat(PreferenceKeys.PREF_KEY_LNG, 0F).toDouble()

            val r = 6371

            val latDistance = Math.toRadians(lat2 - lat1)
            val lngDistance = Math.toRadians(lng2 - lng1)

            val a = sin(latDistance / 2) * sin(latDistance / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(lngDistance / 2) * sin(lngDistance / 2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))

            var distance = r * c * 1000
            distance = distance.pow(2)
            return sqrt(distance)
        }
    }
}