package com.hidesign.hiweather.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun getDateTime(pattern: String, timeInt: Long): String {
        val date = Date(timeInt * 1000L)
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

    fun getDayOfWeekText(day: String): String {
        return when (day) {
            "1" -> {
                "Monday"
            }
            "2" -> {
                "Tuesday"
            }
            "3" -> {
                "Wednesday"
            }
            "4"-> {
                "Thursday"
            }
            "5" -> {
                "Friday"
            }
            "6" -> {
                "Saturday"
            }
            "7"-> {
                "Sunday"
            }
            else -> { "Unknown" }
        }
    }
}