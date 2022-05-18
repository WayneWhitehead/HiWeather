package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class AdministrativeArea(
    @SerializedName("CountryID")
    var countryID: String,
    @SerializedName("EnglishName")
    var englishName: String,
    @SerializedName("EnglishType")
    var englishType: String,
    @SerializedName("ID")
    var iD: String,
    @SerializedName("Level")
    var level: Int,
    @SerializedName("LocalizedName")
    var localizedName: String,
    @SerializedName("LocalizedType")
    var localizedType: String
)