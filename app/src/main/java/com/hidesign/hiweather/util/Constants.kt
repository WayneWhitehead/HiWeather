package com.hidesign.hiweather.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

object Constants {

    const val preferences = "userPreferences"
    const val latitude = "userLatitude"
    const val longitude = "userLongitude"
    const val location_json = "location_json"
    const val refresh = "refreshButtonClick"
    const val app_open = "widgetAppOpen"
    const val auto_update = "AUTO_UPDATE"
    const val app_update = 100

    //AIR Constants
    const val airItemScreeName = "Air Quality Item"
    const val carbon_monoxide = "Carbon Monoxide(CO)"
    const val ammonia = "Ammonia(NH₃)"
    const val nitrogen_dioxide = "Nitrogen Dioxide(NO₂)"
    const val ozone = "Ozone(O₃)"
    const val coarse_particle_matter = "Coarse Particle Matter(PM₁₀)"
    const val fine_particle_matter = "Fine Particle Matter(PM₂₅)"
    const val sulphur_dioxide = "Sulphur Dioxide(SO₂)"

    //API KEYS
    const val placesKey = "placesKey"
    const val openWeatherKey = "weatherKey"
    fun getAPIKey(context: Context, key: String): String {
        val ai: ApplicationInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData[key]
        return value.toString()
    }

    const val refreshInterval = "refreshInterval"
    const val temperatureUnit = "temperatureUnit"

}