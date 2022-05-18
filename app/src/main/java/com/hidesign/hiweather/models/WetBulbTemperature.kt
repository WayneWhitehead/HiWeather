package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class WetBulbTemperature(
    @SerializedName("Metric")
    var metric: Metric
)