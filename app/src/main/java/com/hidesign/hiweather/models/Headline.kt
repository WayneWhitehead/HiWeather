package com.hidesign.hiweather.models


import com.google.gson.annotations.SerializedName

data class Headline(
    @SerializedName("Category")
    var category: String,
    @SerializedName("EffectiveDate")
    var effectiveDate: String,
    @SerializedName("EffectiveEpochDate")
    var effectiveEpochDate: Int,
    @SerializedName("EndDate")
    var endDate: String,
    @SerializedName("EndEpochDate")
    var endEpochDate: Int,
    @SerializedName("Link")
    var link: String,
    @SerializedName("MobileLink")
    var mobileLink: String,
    @SerializedName("Severity")
    var severity: Int,
    @SerializedName("Text")
    var text: String
)