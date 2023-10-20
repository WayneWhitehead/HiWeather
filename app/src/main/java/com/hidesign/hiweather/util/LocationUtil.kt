package com.hidesign.hiweather.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import timber.log.Timber
import java.io.IOException
import java.util.Locale

class LocationUtil(private val context: Context) {

    private val locationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(onSuccess: (Address) -> Unit, onFailure: () -> Unit) {
        locationProviderClient.lastLocation.addOnCompleteListener { lastLocation ->
            if (lastLocation.isSuccessful && lastLocation.result != null) {
                handleLocation(lastLocation.result, onSuccess, onFailure)
            } else {
                getCurrentLocation(onSuccess, onFailure)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(onSuccess: (Address) -> Unit, onFailure: () -> Unit) {
        locationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnCompleteListener { currentLocation ->
            if (currentLocation.isSuccessful && currentLocation.result != null) {
                handleLocation(currentLocation.result, onSuccess, onFailure)
            } else {
                onFailure()
            }
        }
    }

    private fun handleLocation(location: Location, onSuccess: (Address) -> Unit, onFailure: () -> Unit) {
        var address: Address? = null
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            address = geocoder.getFromLocation(location.latitude, location.longitude, 1)?.get(0)
        } catch (e: IOException) {
            e.printStackTrace()
            Timber.tag("Tag").e("Error getting Street Address: ")
        }

        if (address != null) {
            onSuccess(address)
        } else {
            onFailure()
        }
    }
}
