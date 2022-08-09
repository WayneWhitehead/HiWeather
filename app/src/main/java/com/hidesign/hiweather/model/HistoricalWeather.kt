package com.hidesign.hiweather.model


import androidx.room.Embedded
import com.google.gson.annotations.SerializedName

data class HistoricalWeather(
    @SerializedName("data")
    @Embedded var `data`: List<Data>,
    @SerializedName("lat")
    var lat: Double,
    @SerializedName("lon")
    var lon: Double,
    @SerializedName("timezone")
    var timezone: String,
    @SerializedName("timezone_offset")
    var timezoneOffset: Int,
)

data class Data(
    @SerializedName("clouds")
    var clouds: Int,
    @SerializedName("dew_point")
    var dewPoint: Double,
    @SerializedName("dt")
    var dt: Int,
    @SerializedName("feels_like")
    var feelsLike: Double,
    @SerializedName("humidity")
    var humidity: Int,
    @SerializedName("pressure")
    var pressure: Int,
    @SerializedName("sunrise")
    var sunrise: Int,
    @SerializedName("sunset")
    var sunset: Int,
    @SerializedName("temp")
    var temp: Double,
    @SerializedName("uvi")
    var uvi: Double,
    @SerializedName("visibility")
    var visibility: Int,
    @SerializedName("weather")
    @Embedded var weather: List<Weather>,
    @SerializedName("wind_deg")
    var windDeg: Int,
    @SerializedName("wind_speed")
    var windSpeed: Double,
)