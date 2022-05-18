package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class Wind(
    @SerializedName("Direction")
    var direction: Direction,
    @SerializedName("Speed")
    var speed: Speed
)