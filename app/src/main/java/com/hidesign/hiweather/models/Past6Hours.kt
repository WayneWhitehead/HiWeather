package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class Past6Hours(
    @SerializedName("Metric")
    var metric: Metric
)