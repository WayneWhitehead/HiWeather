package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class Past24HourTemperatureDeparture(
    @SerializedName("Metric")
    var metric: Metric
)