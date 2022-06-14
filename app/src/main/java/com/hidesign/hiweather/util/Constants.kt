package com.hidesign.hiweather.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

object Constants {

    const val lastFetch = "LAST_FETCH"
    const val userPreferences = "userPreferences"

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
        val ai: ApplicationInfo = context.packageManager
            .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData[key]
        return value.toString()
    }
}