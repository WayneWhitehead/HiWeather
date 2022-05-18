package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class Day(
    @SerializedName("CloudCover")
    var cloudCover: Int,
    @SerializedName("Evapotranspiration")
    var evapotranspiration: Evapotranspiration,
    @SerializedName("HasPrecipitation")
    var hasPrecipitation: Boolean,
    @SerializedName("HoursOfIce")
    var hoursOfIce: Int,
    @SerializedName("HoursOfPrecipitation")
    var hoursOfPrecipitation: Int,
    @SerializedName("HoursOfRain")
    var hoursOfRain: Int,
    @SerializedName("HoursOfSnow")
    var hoursOfSnow: Int,
    @SerializedName("Ice")
    var ice: Ice,
    @SerializedName("IceProbability")
    var iceProbability: Int,
    @SerializedName("Icon")
    var icon: Int,
    @SerializedName("IconPhrase")
    var iconPhrase: String,
    @SerializedName("LongPhrase")
    var longPhrase: String,
    @SerializedName("PrecipitationIntensity")
    var precipitationIntensity: String,
    @SerializedName("PrecipitationProbability")
    var precipitationProbability: Int,
    @SerializedName("PrecipitationType")
    var precipitationType: String,
    @SerializedName("Rain")
    var rain: Rain,
    @SerializedName("RainProbability")
    var rainProbability: Int,
    @SerializedName("ShortPhrase")
    var shortPhrase: String,
    @SerializedName("Snow")
    var snow: Snow,
    @SerializedName("SnowProbability")
    var snowProbability: Int,
    @SerializedName("SolarIrradiance")
    var solarIrradiance: SolarIrradiance,
    @SerializedName("ThunderstormProbability")
    var thunderstormProbability: Int,
    @SerializedName("TotalLiquid")
    var totalLiquid: TotalLiquid,
    @SerializedName("Wind")
    var wind: Wind,
    @SerializedName("WindGust")
    var windGust: WindGust
)