package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class TemperatureSummary(
    @SerializedName("Past12HourRange")
    var past12HourRange: Past12HourRange,
    @SerializedName("Past24HourRange")
    var past24HourRange: Past24HourRange,
    @SerializedName("Past6HourRange")
    var past6HourRange: Past6HourRange
)