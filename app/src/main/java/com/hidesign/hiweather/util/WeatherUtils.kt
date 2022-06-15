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
            in 0..4 -> "NNW"
            in 5..26 -> "N"
            in 27..49 -> "NNE"
            in 50..71 -> "NE"
            in 72..94 -> "ENE"
            in 95..116 -> "E"
            in 117..139 -> "ESE"
            in 140..161 -> "SE"
            in 162..184 -> "SSE"
            in 185..206 -> "S"
            in 207..229 -> "SSW"
            in 230..251 -> "SW"
            in 252..274 -> "WSW"
            in 275..296 -> "W"
            in 297..319 -> "WNW"
            in 320..342 -> "NW"
            in 343..360 -> "NNW"
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