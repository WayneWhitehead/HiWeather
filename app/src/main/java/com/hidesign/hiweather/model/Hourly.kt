package com.hidesign.hiweather.model

import com.google.gson.annotations.SerializedName

data class Hourly(
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
    @SerializedName("pop")
    var pop: Double,
    @SerializedName("pressure")
    var pressure: Int,
    @SerializedName("temp")
    var temp: Double,
    @SerializedName("uvi")
    var uvi: Double,
    @SerializedName("visibility")
    var visibility: Int,
    @SerializedName("weather")
    var weather: List<Weather>,
    @SerializedName("wind_deg")
    var windDeg: Int,
    @SerializedName("wind_gust")
    var windGust: Double,
    @SerializedName("wind_speed")
    var windSpeed: Double,
)