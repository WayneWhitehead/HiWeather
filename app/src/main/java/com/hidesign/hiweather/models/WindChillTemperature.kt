package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class WindChillTemperature(
    @SerializedName("Metric")
    var metric: Metric
)