package com.hidesign.hiweather.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.*
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
            val lastLocationRequest = LastLocationRequest.Builder().apply {
                setMaxUpdateAgeMillis(10000)
                setGranularity(Granularity.GRANULARITY_COARSE)
            }

            locationProviderClient.getLastLocation(lastLocationRequest.build()).await()?.let {
                getAddressFromLocation(it)
            }?: run {
                val currentLocationRequest = CurrentLocationRequest.Builder().apply {
                    setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    setMaxUpdateAgeMillis(10000)
                    setGranularity(Granularity.GRANULARITY_COARSE)
                }
                locationProviderClient.getCurrentLocation(currentLocationRequest.build(), null).await()?.let {
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
            putFloat(Constants.LATITUDE, address.latitude.toFloat())
            putFloat(Constants.LONGITUDE, address.longitude.toFloat())
            putString(Constants.LOCALITY, address.locality ?: address.featureName)
            apply()
        }
    }
}