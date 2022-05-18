package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class Speed(
    @SerializedName("Metric")
    var metric: Metric
)