package com.hidesign.hiweather.util

import com.hidesign.hiweather.R

object WeatherUtils {

    fun getMoonIcon(id: Double): Int {
        return when (id) {
            in 0.0..0.1 -> R.drawable.new_moon
            in 0.11..0.19 -> R.drawable.waxing_moon_2
            in 0.2..0.3 -> R.drawable.first_quarter_moon
            in 0.31..0.39 -> R.drawable.waxing_moon
            in 0.4..0.6 -> R.drawable.full_moon
            in 0.61..0.69 -> R.drawable.waning_moon
            in 0.7..0.8 -> R.drawable.last_quarter_moon
            in 0.81..0.89 -> R.drawable.waning_moon_2
            in 0.9..1.0 -> R.drawable.new_moon
            else -> R.drawable.full_moon
        }
    }

    fun getWindDegreeText(deg: Int): String {
        return when (deg) {
            in 0..11 -> "N"
            in 12..34 -> "NNE"
            in 35..56 -> "NE"
            in 57..79 -> "ENE"
            in 80..101 -> "E"
            in 102..124 -> "ESE"
            in 125..146 -> "SE"
            in 147..169 -> "SSE"
            in 170..191 -> "S"
            in 192..214 -> "SSW"
            in 215..236 -> "SW"
            in 237..259 -> "WSW"
            in 260..281 -> "W"
            in 281..304 -> "WNW"
            in 305..327 -> "NW"
            in 328..349 -> "NNW"
            in 350..360 -> "N"
            else -> "?"
        }
    }

    fun getWeatherIconUrl(icon: String): String {
        val base = "https://openweathermap.org/img/wn/"
        val suffix = "@2x.png"
        return base + icon + suffix
    }

    fun getCurrentActiveSeriesItem(valueArray: IntArray, current: Float): Int {
        return when (current) {
            in 0F..valueArray[0].toFloat() -> 0
            in valueArray[0].toFloat()..valueArray[1].toFloat() -> 1
            in valueArray[1].toFloat()..valueArray[2].toFloat() -> 2
            in valueArray[2].toFloat()..valueArray[3].toFloat() -> 3
            in valueArray[3].toFloat()..valueArray[4].toFloat() -> 4
            in valueArray[4].toFloat()..valueArray[5].toFloat() -> 5
            in valueArray[5].toFloat()..valueArray[6].toFloat() -> 6
            else -> -1
        }
    }

    fun getAirCardBackground(value: Int): Int {
        return when (value) {
            1 -> R.color.airIndex1
            2 -> R.color.airIndex2
            3 -> R.color.airIndex3
            4 -> R.color.airIndex4
            5 -> R.color.airIndex5
            else -> {
                R.color.airIndex1
            }
        }
    }

    fun getAirQualityText(index: Int): String {
        return when (index) {
            1 -> "Good"
            2 -> "Fair"
            3 -> "Moderate"
            4 -> "Poor"
            5 -> "Very Poor"
            else -> "Unknown"
        }
    }
}