package com.hidesign.hiweather.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class LocationUtil @Inject constructor(
    private val context: Context,
    private val locationProviderClient: FusedLocationProviderClient,
    private val geocoder: Geocoder
) {

    @SuppressLint("MissingPermission")
    suspend fun getLocation(): Address? = withContext(Dispatchers.IO) {
        try {
            val location = locationProviderClient.lastLocation.await()
            location?.let {
                getAddressFromLocation(it)
            } ?: run {
                val currentLocation = locationProviderClient.getCurrentLocation(Priority.PRIORITY_LOW_POWER, null).await()
                currentLocation?.let {
                    getAddressFromLocation(it)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getAddressFromLocation(location: Location): Address? = withContext(Dispatchers.IO) {
        try {
            val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)?.get(0)
            address?.let {
                saveLocationInPreferences(it)
            }
            address
        } catch (e: IOException) {
            null
        }
    }

    private suspend fun saveLocationInPreferences(address: Address) = withContext(Dispatchers.IO) {
        val prefs: SharedPreferences = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString(Constants.LATITUDE, address.latitude.toString())
            putString(Constants.LONGITUDE, address.longitude.toString())
            putString(Constants.LOCALITY, address.locality)
            apply()
        }
    }
}