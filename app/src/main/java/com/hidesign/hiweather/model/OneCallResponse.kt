package com.hidesign.hiweather.model

import com.google.gson.annotations.SerializedName
import com.hidesign.hiweather.model.Alerts
import com.hidesign.hiweather.model.Current
import com.hidesign.hiweather.model.Daily
import com.hidesign.hiweather.model.Hourly

data class OneCallResponse(
    @SerializedName("current")
    var current: Current,
    @SerializedName("daily")
    var daily: List<Daily>,
    @SerializedName("hourly")
    var hourly: List<Hourly>,
    @SerializedName("lat")
    var lat: Double,
    @SerializedName("lon")
    var lon: Double,
    @SerializedName("timezone")
    var timezone: String,
    @SerializedName("timezone_offset")
    var timezoneOffset: Int,
    @SerializedName("alerts")
    var alerts: List<Alerts>
)
