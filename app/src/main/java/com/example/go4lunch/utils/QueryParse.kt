package com.example.go4lunch.utils

import android.location.Location

class QueryParse {
    companion object {
        fun parseLocation(location: Location) : String {
            return location.latitude.toString() + ", " + location.longitude
        }
    }
}