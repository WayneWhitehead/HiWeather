package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class DewPoint(
    @SerializedName("Metric")
    var metric: Metric
)