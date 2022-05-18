package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class Elevation(
    @SerializedName("Imperial")
    var imperial: Imperial,
    @SerializedName("Metric")
    var metric: MetricX
)