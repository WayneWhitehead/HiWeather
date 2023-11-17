package com.hidesign.hiweather.util

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class LocationUtil @Inject constructor(private val locationProviderClient: FusedLocationProviderClient, private val geocoder: Geocoder) {

    @SuppressLint("MissingPermission")
    fun getLastLocation(callback: AddressCallback) {
        locationProviderClient.lastLocation.addOnCompleteListener { lastLocation ->
            if (lastLocation.isSuccessful && lastLocation.result != null) {
                handleLocation(lastLocation.result, callback)
            } else {
                getCurrentLocation(callback)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(callback: AddressCallback) {
        locationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnCompleteListener { currentLocation ->
            if (currentLocation.isSuccessful && currentLocation.result != null) {
                handleLocation(currentLocation.result, callback)
            } else {
                callback.onFailure()
            }
        }
    }

    fun handleLocation(location: Location, callback: AddressCallback) {
        val address: Address?
        try {
            address = geocoder.getFromLocation(location.latitude, location.longitude, 1)?.get(0)
        } catch (e: IOException) {
            Timber.tag("Tag").e("Error getting Street Address: ")
            callback.onFailure()
            return
        }
        if (address != null) {
            callback.onSuccess(address)
        } else {
            callback.onFailure()
        }
    }

    interface AddressCallback {
        fun onSuccess(address: Address)
        fun onFailure()
    }
}
