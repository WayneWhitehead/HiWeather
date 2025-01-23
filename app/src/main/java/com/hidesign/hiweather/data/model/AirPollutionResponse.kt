package com.hidesign.hiweather.data.model


import android.net.Uri
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AirPollutionResponse(
    @SerializedName("coord") var coord: Coord? = null,
    @SerializedName("list") var list: List<DefaultAir> = listOf()
): Serializable

data class DefaultAir(
    @SerializedName("components") var components: Components,
    @SerializedName("dt") var dt: Int,
    @SerializedName("main") var main: Main,
): Serializable

data class Main(
    @SerializedName("aqi") var aqi: Int,
): Serializable

data class Coord(
    @SerializedName("lat") var lat: Double,
    @SerializedName("lon") var lon: Double,
): Serializable

data class Components(
    @SerializedName("co") var co: Double = 0.0,
    @SerializedName("nh3") var nh3: Double = 0.0,
    @SerializedName("no") var no: Double = 0.0,
    @SerializedName("no2") var no2: Double = 0.0,
    @SerializedName("o3") var o3: Double = 0.0,
    @SerializedName("pm10") var pm10: Double = 0.0,
    @SerializedName("pm2_5") var pm25: Double = 0.0,
    @SerializedName("so2") var so2: Double = 0.0,
): Serializable {
    fun toJson(): String = Uri.encode(Gson().toJson(this))
}