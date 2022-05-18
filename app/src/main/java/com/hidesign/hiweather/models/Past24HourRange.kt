package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class Past24HourRange(
    @SerializedName("Maximum")
    var maximum: MaximumX,
    @SerializedName("Minimum")
    var minimum: MinimumX
)