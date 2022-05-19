package com.hidesign.hiweather.model


import com.google.gson.annotations.SerializedName

data class DefaultAir (
    @SerializedName("components")
    var components: Components,
    @SerializedName("dt")
    var dt: Int,
    @SerializedName("main")
    var main: Main
)