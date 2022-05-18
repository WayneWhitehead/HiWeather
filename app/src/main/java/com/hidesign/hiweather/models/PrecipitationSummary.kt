package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class PrecipitationSummary(
    @SerializedName("Past12Hours")
    var past12Hours: Past12Hours,
    @SerializedName("Past18Hours")
    var past18Hours: Past18Hours,
    @SerializedName("Past24Hours")
    var past24Hours: Past24Hours,
    @SerializedName("Past3Hours")
    var past3Hours: Past3Hours,
    @SerializedName("Past6Hours")
    var past6Hours: Past6Hours,
    @SerializedName("Past9Hours")
    var past9Hours: Past9Hours,
    @SerializedName("PastHour")
    var pastHour: PastHour,
    @SerializedName("Precipitation")
    var precipitation: Precipitation
)