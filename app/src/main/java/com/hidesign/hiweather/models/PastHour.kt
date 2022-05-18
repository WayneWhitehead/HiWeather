package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class PastHour(
    @SerializedName("Metric")
    var metric: Metric
)