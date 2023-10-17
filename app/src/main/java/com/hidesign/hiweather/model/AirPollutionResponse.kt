package com.hidesign.hiweather.model


import com.google.gson.annotations.SerializedName

data class AirPollutionResponse(
    @SerializedName("coord")
    var coord: Coord? = null,
    @SerializedName("list")
    var list: List<DefaultAir> = listOf()
)

data class DefaultAir(
    @SerializedName("components")
    var components: Components,
    @SerializedName("dt")
    var dt: Int,
    @SerializedName("main")
    var main: Main,
)

data class Main(
    @SerializedName("aqi")
    var aqi: Int,
)

data class Coord(
    @SerializedName("lat")
    var lat: Double,
    @SerializedName("lon")
    var lon: Double,
)

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
    var so2: Double,
)