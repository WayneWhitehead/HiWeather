package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class Pressure(
    @SerializedName("Metric")
    var metric: Metric
)