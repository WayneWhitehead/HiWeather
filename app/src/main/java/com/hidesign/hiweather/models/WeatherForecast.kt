package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class WeatherForecast(
    @SerializedName("DailyForecasts")
    var dailyForecasts: List<DailyForecast>,
    @SerializedName("Headline")
    var headline: Headline
)