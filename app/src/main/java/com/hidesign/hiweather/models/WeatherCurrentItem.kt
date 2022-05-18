package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class WeatherCurrentItem(
    @SerializedName("ApparentTemperature")
    var apparentTemperature: ApparentTemperature,
    @SerializedName("Ceiling")
    var ceiling: Ceiling,
    @SerializedName("CloudCover")
    var cloudCover: Int,
    @SerializedName("DewPoint")
    var dewPoint: DewPoint,
    @SerializedName("EpochTime")
    var epochTime: Int,
    @SerializedName("HasPrecipitation")
    var hasPrecipitation: Boolean,
    @SerializedName("IndoorRelativeHumidity")
    var indoorRelativeHumidity: Int,
    @SerializedName("IsDayTime")
    var isDayTime: Boolean,
    @SerializedName("Link")
    var link: String,
    @SerializedName("LocalObservationDateTime")
    var localObservationDateTime: String,
    @SerializedName("MobileLink")
    var mobileLink: String,
    @SerializedName("ObstructionsToVisibility")
    var obstructionsToVisibility: String,
    @SerializedName("Past24HourTemperatureDeparture")
    var past24HourTemperatureDeparture: Past24HourTemperatureDeparture,
    @SerializedName("Precip1hr")
    var precip1hr: Precip1hr,
    @SerializedName("PrecipitationSummary")
    var precipitationSummary: PrecipitationSummary,
    @SerializedName("PrecipitationType")
    var precipitationType: Any?,
    @SerializedName("Pressure")
    var pressure: Pressure,
    @SerializedName("PressureTendency")
    var pressureTendency: PressureTendency,
    @SerializedName("RealFeelTemperature")
    var realFeelTemperature: RealFeelTemperature,
    @SerializedName("RealFeelTemperatureShade")
    var realFeelTemperatureShade: RealFeelTemperatureShade,
    @SerializedName("RelativeHumidity")
    var relativeHumidity: Int,
    @SerializedName("Temperature")
    var temperature: Temperature,
    @SerializedName("TemperatureSummary")
    var temperatureSummary: TemperatureSummary,
    @SerializedName("UVIndex")
    var uVIndex: Int,
    @SerializedName("UVIndexText")
    var uVIndexText: String,
    @SerializedName("Visibility")
    var visibility: Visibility,
    @SerializedName("WeatherIcon")
    var weatherIcon: Int,
    @SerializedName("WeatherText")
    var weatherText: String,
    @SerializedName("WetBulbTemperature")
    var wetBulbTemperature: WetBulbTemperature,
    @SerializedName("Wind")
    var wind: Wind,
    @SerializedName("WindChillTemperature")
    var windChillTemperature: WindChillTemperature,
    @SerializedName("WindGust")
    var windGust: WindGust
)