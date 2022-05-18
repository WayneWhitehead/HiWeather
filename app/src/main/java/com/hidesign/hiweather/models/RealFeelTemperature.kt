package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class RealFeelTemperature(
    @SerializedName("Maximum")
    var maximum: Maximum,
    @SerializedName("Minimum")
    var minimum: Minimum
)