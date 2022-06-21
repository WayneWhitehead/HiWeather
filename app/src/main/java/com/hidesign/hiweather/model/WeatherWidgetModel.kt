package com.hidesign.hiweather.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class WeatherWidgetModel(
    @PrimaryKey var dt: Int? = 0,
    var temp: Double? = 0.0,
    var feelsLike: Double? = 0.0,
    var uvi: Double? = 0.0,
    var pop: Double? = 0.0,
    var humidity: Int? = 0,
    var clouds: Int? = 0,

    var timezone: String? = TimeZone.getDefault().toString(),
    var icon: String? = "10d",
)