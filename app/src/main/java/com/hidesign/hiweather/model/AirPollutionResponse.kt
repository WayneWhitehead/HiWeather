package com.hidesign.hiweather.model


import com.google.gson.annotations.SerializedName

data class AirPollutionResponse(
    @SerializedName("coord")
    var coord: Coord,
    @SerializedName("list")
    var list: List<DefaultAir>
)