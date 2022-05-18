package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class ApparentTemperature(
    @SerializedName("Metric")
    var metric: Metric
)