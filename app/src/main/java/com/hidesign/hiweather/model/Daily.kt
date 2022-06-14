package com.hidesign.hiweather.model


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Daily(
    @SerializedName("dt")
    @PrimaryKey var dt: Int? = 0,
    @SerializedName("clouds")
    var clouds: Int? = 0,
    @SerializedName("dew_point")
    var dewPoint: Double? = 0.0,
    @SerializedName("feels_like")
    @Ignore var feelsLike: FeelsLike? = null,
    @SerializedName("humidity")
    var humidity: Int? = 0,
    @SerializedName("moon_phase")
    @Ignore var moonPhase: Double? = null,
    @SerializedName("moonrise")
    @Ignore var moonrise: Int? = null,
    @SerializedName("moonset")
    @Ignore var moonset: Int? = null,
    @SerializedName("pop")
    var pop: Double? = 0.0,
    @SerializedName("pressure")
    var pressure: Int? = 0,
    @SerializedName("rain")
    @Ignore var rain: Double? = null,
    @SerializedName("sunrise")
    @Ignore var sunrise: Int? = null,
    @SerializedName("sunset")
    @Ignore var sunset: Int? = null,
    @SerializedName("temp")
    @Ignore var temp: Temp? = null,
    @SerializedName("uvi")
    var uvi: Double? = 0.0,
    @SerializedName("weather")
    @Ignore var weather: List<Weather>? = null,
    @SerializedName("wind_deg")
    var windDeg: Int? = 0,
    @SerializedName("wind_gust")
    @Ignore var windGust: Double? = null,
    @SerializedName("wind_speed")
    var windSpeed: Double? = 0.0,
)