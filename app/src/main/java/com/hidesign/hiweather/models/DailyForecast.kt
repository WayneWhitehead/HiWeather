package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class DailyForecast(
    @SerializedName("AirAndPollen")
    var airAndPollen: List<AirAndPollen>,
    @SerializedName("Date")
    var date: String,
    @SerializedName("Day")
    var day: Day,
    @SerializedName("DegreeDaySummary")
    var degreeDaySummary: DegreeDaySummary,
    @SerializedName("EpochDate")
    var epochDate: Int,
    @SerializedName("HoursOfSun")
    var hoursOfSun: Double,
    @SerializedName("Link")
    var link: String,
    @SerializedName("MobileLink")
    var mobileLink: String,
    @SerializedName("Moon")
    var moon: Moon,
    @SerializedName("Night")
    var night: Night,
    @SerializedName("RealFeelTemperature")
    var realFeelTemperature: RealFeelTemperature,
    @SerializedName("RealFeelTemperatureShade")
    var realFeelTemperatureShade: RealFeelTemperatureShade,
    @SerializedName("Sources")
    var sources: List<String>,
    @SerializedName("Sun")
    var sun: Sun,
    @SerializedName("Temperature")
    var temperature: Temperature
)