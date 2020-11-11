package com.example.go4lunch

import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.example.go4lunch.models.restaurant.Close
import com.example.go4lunch.models.restaurant.Period
import com.example.go4lunch.utils.APIParse
import com.example.go4lunch.utils.QueryParse
import com.google.android.gms.maps.model.LatLng
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Before

class Go4LunchUnitTests {

    @Test
    fun validateParsedAddress() {
        val expectedResult = "1060 Rengstorf Ave"

        assertEquals(expectedResult, APIParse.parseAddress("1060 Rengstorf Ave, Mountain View"))
    }
}