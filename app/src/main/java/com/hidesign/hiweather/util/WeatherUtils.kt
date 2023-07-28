package com.hidesign.hiweather.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.hidesign.hiweather.R

object WeatherUtils {

    fun getMoonIcon(id: Double): Int {
        return when (id) {
            in 0.0..0.1 -> R.drawable.full_moon
            in 0.11..0.19 -> R.drawable.waxing_moon_2
            in 0.2..0.3 -> R.drawable.first_quarter_moon
            in 0.31..0.39 -> R.drawable.waxing_moon
            in 0.4..0.6 -> R.drawable.new_moon
            in 0.61..0.69 -> R.drawable.waning_moon
            in 0.7..0.8 -> R.drawable.last_quarter_moon
            in 0.81..0.89 -> R.drawable.waning_moon_2
            in 0.9..1.0 -> R.drawable.full_moon
            else -> R.drawable.full_moon
        }
    }

    fun getWindDegreeText(deg: Int): String {
        return when (deg) {
            in 347..360 -> "N"
            in 0..11 -> "N"
            in 12..33 -> "NNE"
            in 34..55 -> "NE"
            in 56..77 -> "ENE"
            in 78..101 -> "E"
            in 101..122 -> "ESE"
            in 123..144 -> "SE"
            in 145..166 -> "SSE"
            in 167..190 -> "S"
            in 191..212 -> "SSW"
            in 213..234 -> "SW"
            in 235..256 -> "WSW"
            in 257..280 -> "W"
            in 281..302 -> "WNW"
            in 303..324 -> "NW"
            in 325..346 -> "NNW"
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

    fun getAirQualityColour(index: Int, context: Context): Int {
        return ContextCompat.getColor(context, when (index) {
            1 -> R.color.airIndex1
            2 -> R.color.airIndex2
            3 -> R.color.airIndex3
            4 -> R.color.airIndex4
            else -> R.color.airIndex5
        })
    }
}