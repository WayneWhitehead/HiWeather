package com.hidesign.hiweather.util

import com.hidesign.hiweather.R

object WeatherUtils {

    fun getMoonIcon(id: Double): Int {
        return when (id) {
            in 0.0..0.1 -> {
                R.drawable.new_moon
            }
            in 0.11..0.19 -> {
                R.drawable.waning_moon
            }
            in 0.2..0.3 -> {
                R.drawable.last_quarter_moon
            }
            in 0.31..0.39 -> {
                R.drawable.waning_moon_2
            }
            in 0.4..0.6 -> {
                R.drawable.full_moon
            }
            in 0.61..0.69 -> {
                R.drawable.waxing_moon_2
            }
            in 0.7..0.8 -> {
                R.drawable.first_quarter_moon
            }
            in 0.81..0.89 -> {
                R.drawable.waxing_moon
            }
            in 0.9..1.0 -> {
                R.drawable.new_moon
            }
            else -> {

                R.drawable.full_moon
            }
        }
    }

    fun getWindDegreeText(deg: Int): String {
        return when (deg) {
            in 0..45 -> {
                "S"
            }
            in 46..90 -> {
                "SW"
            }
            in 91..135 -> {
                "W"
            }
            in 136..180 -> {
                "NW"
            }
            in 181..225 -> {
                "N"
            }
            in 226..270 -> {
                "NE"
            }
            in 271..315 -> {
                "E"
            }
            in 316..360 -> {
                "SE"
            }
            else -> {
                "?"
            }
        }
    }

    fun getWeatherIcon(id: Int): Int {
        return when (id) {
            in 800..800 -> {
                R.drawable.sun
            }
            in 801..804 -> {
                R.drawable.overcast
            }
            in 200..599 -> {
                R.drawable.rain
            }
            else -> {
                R.drawable.sun
            }
        }
    }

    fun getValueQualityText(
        airStrings: Array<String>,
        airValues: IntArray,
        current: Float,
    ): String {
        return when (current.toInt()) {
            in airValues[0]..airValues[1] -> airStrings[0]
            in airValues[1]..airValues[2] -> airStrings[1]
            in airValues[2]..airValues[3] -> airStrings[2]
            in airValues[3]..airValues[4] -> airStrings[3]
            in airValues[4]..airValues[5] -> airStrings[4]
            else -> "Unknown"
        }
    }

    fun getCurrentActiveSeriesItem(valueArray: IntArray, current: Float): Int {
        return when (current) {
            in 0F..valueArray[0].toFloat() -> {
                0
            }
            in valueArray[0].toFloat()..valueArray[1].toFloat() -> {
                1
            }
            in valueArray[1].toFloat()..valueArray[2].toFloat() -> {
                2
            }
            in valueArray[2].toFloat()..valueArray[3].toFloat() -> {
                3
            }
            in valueArray[3].toFloat()..valueArray[4].toFloat() -> {
                4
            }
            in valueArray[4].toFloat()..valueArray[5].toFloat() -> {
                5
            }
            in valueArray[5].toFloat()..valueArray[6].toFloat() -> {
                6
            }
            else -> {
                -1
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
            else -> {
                "Unknown"
            }
        }
    }
}