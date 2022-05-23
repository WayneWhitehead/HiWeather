package com.hidesign.hiweather.model


import com.google.gson.annotations.SerializedName
import com.hidesign.hiweather.R

data class Weather(
    @SerializedName("description")
    var description: String,
    @SerializedName("icon")
    var icon: String,
    @SerializedName("id")
    var id: Int,
    @SerializedName("main")
    var main: String
)

object Wind{
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
            else -> { "?" }
        }
    }
}

object WeatherIcon {
    fun getIcon(id: Int): Int{
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
}

object MoonIcon {
    fun getIcon(id: Double): Int{
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
}