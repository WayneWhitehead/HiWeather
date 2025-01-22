package com.hidesign.hiweather.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OneCallResponse(
    @SerializedName("current")
    var current: Current = Current(),
    @SerializedName("daily")
    var daily: List<Daily> = listOf(),
    @SerializedName("hourly")
    var hourly: List<Hourly> = listOf(),
    @SerializedName("lat")
    var lat: Double = 0.0,
    @SerializedName("lon")
    var lon: Double = 0.0,
    @SerializedName("timezone")
    var timezone: String = "",
    @SerializedName("timezone_offset")
    var timezoneOffset: Int = 0,
    @SerializedName("alerts")
    var alerts: List<Alerts> = listOf(),
): Serializable

data class Current(
    @SerializedName("dt")
    var dt: Int = 0,
    @SerializedName("clouds")
    var clouds: Int = 0,
    @SerializedName("dew_point")
    var dewPoint: Double = 0.0,
    @SerializedName("feels_like")
    var feelsLike: Double = 0.0,
    @SerializedName("humidity")
    var humidity: Int = 0,
    @SerializedName("pressure")
    var pressure: Int = 0,
    @SerializedName("sunrise")
    var sunrise: Int = 0,
    @SerializedName("sunset")
    var sunset: Int = 0,
    @SerializedName("temp")
    var temp: Double = 0.0,
    @SerializedName("uvi")
    var uvi: Double = 0.0,
    @SerializedName("visibility")
    var visibility: Int = 0,
    @SerializedName("weather")
    var weather: List<Weather> = listOf(),
    @SerializedName("wind_deg")
    var windDeg: Int = 0,
    @SerializedName("wind_gust")
    var windGust: Double = 0.0,
    @SerializedName("wind_speed")
    var windSpeed: Double = 0.0,
): Serializable

data class Weather(
    @SerializedName("description") var description: String,
    @SerializedName("icon") var icon: String,
    @SerializedName("id") var weatherId: Int,
    @SerializedName("main") var main: String,
): Serializable

abstract class FutureWeather(
    @SerializedName("clouds") var clouds: Int = 0,
    @SerializedName("dew_point") var dewPoint: Double = 0.0,
    @SerializedName("dt") var dt: Int = 0,
    @SerializedName("humidity") var humidity: Int = 0,
    @SerializedName("pop") var pop: Double = 0.0,
    @SerializedName("pressure") var pressure: Int = 0,
    @SerializedName("uvi") var uvi: Double = 0.0,
    @SerializedName("weather") var weather: List<Weather> = listOf(),
    @SerializedName("wind_deg") var windDeg: Int = 0,
    @SerializedName("wind_gust") var windGust: Double = 0.0,
    @SerializedName("wind_speed") var windSpeed: Double = 0.0,
): Serializable {
    constructor() : this(0, 0.0, 0, 0, 0.0, 0, 0.0, listOf(), 0, 0.0, 0.0)
}

data class Daily(
    @SerializedName("feels_like") var feelsLike: FeelsLike = FeelsLike(),
    @SerializedName("moon_phase") var moonPhase: Double = 0.0,
    @SerializedName("moonrise") var moonrise: Int = 0,
    @SerializedName("moonset") var moonset: Int = 0,
    @SerializedName("rain") var rain: Double = 0.0,
    @SerializedName("sunrise") var sunrise: Int = 0,
    @SerializedName("sunset") var sunset: Int = 0,
    @SerializedName("temp") var temp: Temp = Temp(),
    @SerializedName("summary") var summary: String = ""
): FutureWeather(), Serializable

data class Hourly(
    @SerializedName("feels_like") var feelsLike: Double,
    @SerializedName("temp") var temp: Double,
    @SerializedName("visibility") var visibility: Int,
): FutureWeather(), Serializable

data class Alerts(
    @SerializedName("sender_name") var senderName: String,
    @SerializedName("event") var event: String,
    @SerializedName("start") var start: Long,
    @SerializedName("end") var end: Long,
    @SerializedName("description") var description: String,
): Serializable

data class Temp(
    @SerializedName("day") var day: Double = 0.0,
    @SerializedName("eve") var eve: Double = 0.0,
    @SerializedName("max") var max: Double = 0.0,
    @SerializedName("min") var min: Double = 0.0,
    @SerializedName("morn") var morn: Double = 0.0,
    @SerializedName("night") var night: Double = 0.0,
): Serializable

data class FeelsLike(
    @SerializedName("day") var day: Double = 0.0,
    @SerializedName("eve") var eve: Double = 0.0,
    @SerializedName("morn") var morn: Double = 0.0,
    @SerializedName("night") var night: Double = 0.0,
): Serializable