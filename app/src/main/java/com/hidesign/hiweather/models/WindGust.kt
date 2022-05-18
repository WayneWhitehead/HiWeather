package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class WindGust(
    @SerializedName("Direction")
    var direction: Direction,
    @SerializedName("Speed")
    var speed: Speed
)