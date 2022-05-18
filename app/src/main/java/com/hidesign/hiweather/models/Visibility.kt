package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class Visibility(
    @SerializedName("Metric")
    var metric: Metric
)