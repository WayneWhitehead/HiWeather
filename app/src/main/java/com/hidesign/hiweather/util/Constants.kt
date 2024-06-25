package com.hidesign.hiweather.util

import androidx.core.text.util.LocalePreferences
import okhttp3.internal.immutableListOf

object Constants {

    const val PREFERENCES = "userPreferences"
    const val REFRESH = "refreshButtonClick"
    const val APP_OPEN = "widgetAppOpen"
    const val AUTO_UPDATE = "AUTO_UPDATE"

    val refreshIntervals = immutableListOf(
        "Never",
        "1 Hour",
        "2 Hours",
        "4 Hours",
        "6 Hours",
        "12 Hours",
        "24 Hours"
    )

    //AIR Constants
    private const val CARBON_MONOXIDE = "Carbon Monoxide(CO)"
    private const val AMMONIA = "Ammonia(NH₃)"
    private const val NITRIC_OXIDE = "Nitric Oxide(NO)"
    private const val NITROGEN_DIOXIDE = "Nitrogen Dioxide(NO₂)"
    private const val OZONE = "Ozone(O₃)"
    private const val COARSE_MATTER = "Coarse Particle Matter(PM₁₀)"
    private const val FINE_MATTER = "Fine Particle Matter(PM₂₅)"
    private const val SULPHUR_DIOXIDE = "Sulphur Dioxide(SO₂)"

    val airAbbreviations = immutableListOf("CO", "NH₃", "NO", "NO₂", "O₃", "PM₁₀", "PM₂₅", "SO₂")
    val airNamesExpanded = immutableListOf(
        CARBON_MONOXIDE,
        AMMONIA,
        NITRIC_OXIDE,
        NITROGEN_DIOXIDE,
        OZONE,
        COARSE_MATTER,
        FINE_MATTER,
        SULPHUR_DIOXIDE
    )

    //Weather Constants
    const val WEATHER_RESPONSE = "WEATHER_RESPONSE"
    const val LATITUDE = "LATITUDE"
    const val LONGITUDE = "LONGITUDE"
    const val LOCALITY = "LOCALITY"

    fun getUnit(): String {
        val temperatureUnit = LocalePreferences.getTemperatureUnit()
        return when (temperatureUnit) {
            LocalePreferences.TemperatureUnit.CELSIUS -> "metric"
            LocalePreferences.TemperatureUnit.FAHRENHEIT -> "imperial"
            else -> ""
        }
    }
}