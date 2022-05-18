package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class SupplementalAdminArea(
    @SerializedName("EnglishName")
    var englishName: String,
    @SerializedName("Level")
    var level: Int,
    @SerializedName("LocalizedName")
    var localizedName: String
)