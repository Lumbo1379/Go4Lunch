package com.example.go4lunch.utils

import com.example.go4lunch.models.restaurant.Period
import java.util.*


class APIParse {
    companion object {
        fun parseAddress(address: String) : String {
           val strs = address.split(",").toTypedArray()

            return strs[0]
        }

        fun parseOpeningHours(periods: List<Period>, day: Int) : String {
            if (periods[0].close == null)
                return "24/7"

            val relevantPeriods = periods.filter { p -> p.open.day == day }

            return when (relevantPeriods.size) {
                1 -> {
                    if (checkIfClosingSoon(relevantPeriods[0].close.time)) {
                        return "Closing soon"
                    }

                    "Open until " + checkIf24Hours(relevantPeriods[0].close.time / 100) + checkForNot00(relevantPeriods[0].close.time) + checkIfAM(relevantPeriods[0].close.time)
                }
                else -> {
                    if (checkIfClosingSoon(relevantPeriods[0].close.time)) {
                        return "Closing soon"
                    }

                    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 100
                    val period = relevantPeriods.find { p -> p.close.time >= currentHour || p.close.time == 0}

                    if (period != null) {
                        "Open until " + checkIf24Hours(period.close.time / 100) + checkForNot00(period.close.time) + checkIfAM(period.close.time)
                    } else {
                        "Could not retrieve closing time"
                    }
                }
            }
        }

        private fun checkIf24Hours(period: Int) : String {
            if (period == 0) { // 12am, handled in checkForNot00
                return ""
            }

            if (period < 13) {
                return period.toString()
            }

            return (period - 12).toString()
        }

        private fun checkForNot00(period: Int) : String {
            val strPeriod = period.toString()

            if (strPeriod.length == 1) { // 12am
                return "12"
            }

            if (strPeriod[strPeriod.length - 2] != '0' || strPeriod[strPeriod.length - 1] != '0') {
                return ":" + strPeriod[strPeriod.length - 2] + strPeriod[strPeriod.length - 1]
            }

            return ""
        }

        private fun checkIfAM(period: Int) : String {
            if (period < 1200) {
                return "am"
            }

            return "pm"
        }

        private fun checkIfClosingSoon(period: Int) : Boolean {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 100

            var p = period

            if (p == 0) {
                p = 2400
            }

            if (kotlin.math.abs(p - hour) <= 50) { // Half an hour
                return true
            }

            return false
        }
    }
}