package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class Past3Hours(
    @SerializedName("Metric")
    var metric: Metric
)