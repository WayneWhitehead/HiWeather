package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class DegreeDaySummary(
    @SerializedName("Cooling")
    var cooling: Cooling,
    @SerializedName("Heating")
    var heating: Heating
)