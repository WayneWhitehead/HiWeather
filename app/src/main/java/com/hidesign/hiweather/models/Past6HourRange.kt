package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class Past6HourRange(
    @SerializedName("Maximum")
    var maximum: Maximum,
    @SerializedName("Minimum")
    var minimum: Minimum
)