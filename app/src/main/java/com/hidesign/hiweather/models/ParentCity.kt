package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class ParentCity(
    @SerializedName("EnglishName")
    var englishName: String,
    @SerializedName("Key")
    var key: String,
    @SerializedName("LocalizedName")
    var localizedName: String
)