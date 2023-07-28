package com.hidesign.hiweather.model

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class OneCallResponse(
    @SerializedName("current")
    var current: Current,
    @SerializedName("daily")
    var daily: List<Daily>,
    @SerializedName("hourly")
    var hourly: List<Hourly>,
    @SerializedName("lat")
    var lat: Double,
    @SerializedName("lon")
    var lon: Double,
    @SerializedName("timezone")
    var timezone: String,
    @SerializedName("timezone_offset")
    var timezoneOffset: Int,
    @SerializedName("alerts")
    var alerts: List<Alerts>
): java.io.Serializable

data class Current(
    @SerializedName("dt")
    var dt: Int,
    @SerializedName("clouds")
    var clouds: Int,
    @SerializedName("dew_point")
    var dewPoint: Double,
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
    var weather: List<Weather>,
    @SerializedName("wind_deg")
    var windDeg: Int,
    @SerializedName("wind_gust")
    var windGust: Double,
    @SerializedName("wind_speed")
    var windSpeed: Double,
): java.io.Serializable

data class Weather(
    @SerializedName("description")
    var description: String,
    @SerializedName("icon")
    var icon: String,
    @SerializedName("id")
    @PrimaryKey var weatherId: Int,
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
    var sender_name: String,
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