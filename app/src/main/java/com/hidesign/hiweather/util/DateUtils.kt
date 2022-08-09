package com.hidesign.hiweather.util

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

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

    fun getRefreshInterval(activity: Context): Long {
        val sharedPref = activity.getSharedPreferences(Constants.preferences, Context.MODE_PRIVATE)
        return when (sharedPref!!.getInt(Constants.refreshInterval, 1)) {
            0 -> 1L
            1 -> 3L
            2 -> 6L
            3 -> 12L
            4 -> 24L
            else -> 1L
        }
    }
}