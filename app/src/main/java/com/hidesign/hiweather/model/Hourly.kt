package com.hidesign.hiweather.model


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity
data class Hourly(
    @SerializedName("clouds")
    var clouds: Int? = 0,
    @SerializedName("dew_point")
    var dewPoint: Double? = 0.0,
    @SerializedName("dt")
    @PrimaryKey var dt: Int? = 0,
    @SerializedName("feels_like")
    var feelsLike: Double? = 0.0,
    @SerializedName("humidity")
    var humidity: Int? = 0,
    @SerializedName("pop")
    var pop: Double? = 0.0,
    @SerializedName("pressure")
    var pressure: Int? = 0,
    @SerializedName("temp")
    var temp: Double? = 0.0,
    @SerializedName("uvi")
    var uvi: Double? = 0.0,
    @SerializedName("visibility")
    var visibility: Int? = 0,
    @SerializedName("weather")
    @Ignore var weather: List<Weather>? = null,
    @SerializedName("wind_deg")
    var windDeg: Int? = 0,
    @SerializedName("wind_gust")
    @Ignore var windGust: Double? = 0.0,
    @SerializedName("wind_speed")
    var windSpeed: Double? = 0.0,
    var timezone: String? = TimeZone.getDefault().toString(),
)