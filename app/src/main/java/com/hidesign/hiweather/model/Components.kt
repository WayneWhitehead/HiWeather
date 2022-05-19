package com.hidesign.hiweather.model


import com.google.gson.annotations.SerializedName

data class Components(
    @SerializedName("co")
    var co: Double,
    @SerializedName("nh3")
    var nh3: Double,
    @SerializedName("no")
    var no: Double,
    @SerializedName("no2")
    var no2: Double,
    @SerializedName("o3")
    var o3: Double,
    @SerializedName("pm10")
    var pm10: Double,
    @SerializedName("pm2_5")
    var pm25: Double,
    @SerializedName("so2")
    var so2: Double
)