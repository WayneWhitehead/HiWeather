package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class Precip1hr(
    @SerializedName("Metric")
    var metric: Metric
)