package com.hidesign.hiweather.model

import com.google.gson.annotations.SerializedName

data class Alerts(
    @SerializedName("sender_name")
    var sender_name: String,
    @SerializedName("event")
    var event: String,
    @SerializedName("start")
    var start: Long,
    @SerializedName("end")
    var end: Long,
    @SerializedName("description")
    var description: String,
)