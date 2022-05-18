package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class Direction(
    @SerializedName("Degrees")
    var degrees: Int,
    @SerializedName("English")
    var english: String,
    @SerializedName("Localized")
    var localized: String
)