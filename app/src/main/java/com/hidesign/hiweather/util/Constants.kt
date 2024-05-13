package com.hidesign.hiweather.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.hidesign.hiweather.R

object Constants {

    const val PREFERENCES = "userPreferences"
    const val REFRESH = "refreshButtonClick"
    const val APP_OPEN = "widgetAppOpen"
    const val AUTO_UPDATE = "AUTO_UPDATE"

    //AIR Constants
    const val airItemScreeName = "Air Quality Item"
    const val CARBON_MONOXIDE = "Carbon Monoxide(CO)"
    const val AMMONIA = "Ammonia(NH₃)"
    const val NITROGEN_DIOXIDE = "Nitrogen Dioxide(NO₂)"
    const val OZONE = "Ozone(O₃)"
    const val COARSE_MATTER = "Coarse Particle Matter(PM₁₀)"
    const val FINE_MATTER = "Fine Particle Matter(PM₂₅)"
    const val SULPHUR_DIOXIDE = "Sulphur Dioxide(SO₂)"

    //API KEYS
    const val PLACES_KEY = "placesKey"
    const val OPENWEATHER_KEY = "weatherKey"

    //Weather Constants
    const val WEATHER_RESPONSE = "WEATHER_RESPONSE"
    const val LATITUDE = "LATITUDE"
    const val LONGITUDE = "LONGITUDE"
    const val LOCALITY = "LOCALITY"

    fun getAPIKey(context: Context, key: String): String {
        val ai: ApplicationInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData.getString(key, "")
        return value.toString()
    }

    fun getUnit(context: Context): String {
        val sharedPref = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        val posUnit = sharedPref.getInt(TEMPERATURE_UNIT, 0)
        var unit = "celsius"
        for ((pos, value) in context.resources.getStringArray(R.array.temperature_units).withIndex()) {
            if (posUnit == pos) {
                unit = value.lowercase()
            }
        }
        return when (unit) {
            "celsius" -> "metric"
            "fahrenheit" -> "imperial"
            "kelvin" -> ""
            else -> "metric"
        }
    }

    const val REFRESH_INTERVAL = "refreshInterval"
    const val TEMPERATURE_UNIT = "temperatureUnit"
    const val NOTIFICATION_ENABLED = "notificationEnabled"
}