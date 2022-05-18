package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class Moon(
    @SerializedName("Age")
    var age: Int,
    @SerializedName("EpochRise")
    var epochRise: Int,
    @SerializedName("EpochSet")
    var epochSet: Int,
    @SerializedName("Phase")
    var phase: String,
    @SerializedName("Rise")
    var rise: String,
    @SerializedName("Set")
    var `set`: String
)