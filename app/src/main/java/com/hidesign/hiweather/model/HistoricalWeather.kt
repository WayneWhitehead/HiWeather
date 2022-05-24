package com.hidesign.hiweather.model


import com.google.gson.annotations.SerializedName

data class HistoricalWeather(
    @SerializedName("data")
    var `data`: List<Data>,
    @SerializedName("lat")
    var lat: Double,
    @SerializedName("lon")
    var lon: Double,
    @SerializedName("timezone")
    var timezone: String,
    @SerializedName("timezone_offset")
    var timezoneOffset: Int
)