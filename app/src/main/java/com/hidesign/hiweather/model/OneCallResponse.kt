package com.hidesign.hiweather.model

import com.google.gson.annotations.SerializedName

data class OneCallResponse(
    @SerializedName("current")
    var current: Current? = Current(),
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
): java.io.Serializable

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
): java.io.Serializable

data class Weather(
    @SerializedName("description")
    var description: String,
    @SerializedName("icon")
    var icon: String,
    @SerializedName("id")
    var weatherId: Int,
    @SerializedName("main")
    var main: String,
): java.io.Serializable

data class Daily(
    @SerializedName("clouds")
    var clouds: Int,
    @SerializedName("dew_point")
    var dewPoint: Double,
    @SerializedName("dt")
    var dt: Int,
    @SerializedName("feels_like")
    var feelsLike: FeelsLike,
    @SerializedName("humidity")
    var humidity: Int,
    @SerializedName("moon_phase")
    var moonPhase: Double,
    @SerializedName("moonrise")
    var moonrise: Int,
    @SerializedName("moonset")
    var moonset: Int,
    @SerializedName("pop")
    var pop: Double,
    @SerializedName("pressure")
    var pressure: Int,
    @SerializedName("rain")
    var rain: Double,
    @SerializedName("sunrise")
    var sunrise: Int,
    @SerializedName("sunset")
    var sunset: Int,
    @SerializedName("temp")
    var temp: Temp,
    @SerializedName("uvi")
    var uvi: Double,
    @SerializedName("weather")
    var weather: List<Weather>,
    @SerializedName("wind_deg")
    var windDeg: Int? = null,
    @SerializedName("wind_gust")
    var windGust: Double,
    @SerializedName("wind_speed")
    var windSpeed: Double,
    @SerializedName("summary")
    var summary: String
): java.io.Serializable

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
): java.io.Serializable

data class Alerts(
    @SerializedName("sender_name")
    var senderName: String,
    @SerializedName("event")
    var event: String,
    @SerializedName("start")
    var start: Long,
    @SerializedName("end")
    var end: Long,
    @SerializedName("description")
    var description: String,
): java.io.Serializable

data class Temp(
    @SerializedName("day")
    var day: Double,
    @SerializedName("eve")
    var eve: Double,
    @SerializedName("max")
    var max: Double,
    @SerializedName("min")
    var min: Double,
    @SerializedName("morn")
    var morn: Double,
    @SerializedName("night")
    var night: Double,
): java.io.Serializable

data class FeelsLike(
    @SerializedName("day")
    var day: Double,
    @SerializedName("eve")
    var eve: Double,
    @SerializedName("morn")
    var morn: Double,
    @SerializedName("night")
    var night: Double,
): java.io.Serializable