package com.hidesign.hiweather.model


import com.google.gson.annotations.SerializedName

data class Main(
    @SerializedName("aqi")
    var aqi: Int
)