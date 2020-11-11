package com.example.go4lunch

import android.content.Context
import android.content.Intent
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.go4lunch.controllers.LoginActivity
import com.example.go4lunch.controllers.MainActivity
import com.example.go4lunch.models.restaurant.Close
import com.example.go4lunch.models.restaurant.Open
import com.example.go4lunch.models.restaurant.Period
import com.example.go4lunch.utils.APIParse
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class Go4LunchInstrumentedTests {
    private lateinit var mContext: Context

    @Before
    fun setup() {
        mContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun validateRestaurantClosingTimePM() {
        val expectedResult = "Open until 5pm"

        val close = Close(0, 1700)
        val open = Open(0, 1400)
        val period = Period(close, open)
        period.close
        val periods = listOf(period)

        assertEquals(expectedResult, APIParse.parseOpeningHours(periods, 0, mContext))
    }

    @Test
    fun validateRestaurantClosingTimeAM() {
        val expectedResult = "Open until 5am"

        val close = Close(0, 500)
        val open = Open(0, 200)
        val period = Period(close, open)
        period.close
        val periods = listOf(period)

        assertEquals(expectedResult, APIParse.parseOpeningHours(periods, 0, mContext))
    }

    @Test
    fun validateRestaurantClosingSoon() {
        val expectedResult = "Closing soon"

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 100;
        val close = Close(0, hour + 25)
        val open = Open(0, 1400)
        val period = Period(close, open)
        period.close
        val periods = listOf(period)

        assertEquals(expectedResult, APIParse.parseOpeningHours(periods, 0, mContext))
    }
}