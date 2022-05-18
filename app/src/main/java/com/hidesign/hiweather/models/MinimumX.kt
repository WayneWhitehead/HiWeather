package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class MinimumX(
    @SerializedName("Metric")
    var metric: Metric
)