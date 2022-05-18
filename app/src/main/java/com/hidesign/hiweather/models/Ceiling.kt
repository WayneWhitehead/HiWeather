package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class Ceiling(
    @SerializedName("Metric")
    var metric: Metric
)