package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class Precipitation(
    @SerializedName("Metric")
    var metric: Metric
)