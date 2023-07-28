package com.hidesign.hiweather.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    fun getHours(Start: Long, End: Long): String {
        val d1 = Date(Start * 1000L)
        val d2 = Date(End * 1000L)
        val differenceInTime = d2.time - d1.time
        var differenceInMinutes = ((differenceInTime / (1000 * 60)) % 60)
        var differenceInHours = ((differenceInTime / (1000 * 60 * 60)) % 24)
        if (differenceInMinutes < 0) {
            differenceInMinutes *= -1
        }
        if (differenceInHours < 0) {
            differenceInHours *= -1
        }
        return "$differenceInHours HOURS & $differenceInMinutes MINUTES"
    }

    fun getDateTime(pattern: String, timeInt: Long, timezone: String): String {
        val date = Date(timeInt * 1000L)
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        val tz = TimeZone.getTimeZone(timezone)
        sdf.timeZone = tz
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
            "7" -> {
                "Sunday"
            }
            else -> {
                "Unknown"
            }
        }
    }
}