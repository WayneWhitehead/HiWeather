package com.hidesign.hiweather.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LastLocationRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume

@SuppressLint("MissingPermission")
class LocationUtil @Inject constructor(
    @Named("io") private val ioContext: CoroutineContext,
    private val context: Context,
    private val locationProviderClient: FusedLocationProviderClient,
    private val geocoder: Geocoder
) {

    companion object {
        val lastLocationRequest = LastLocationRequest.Builder().apply {
            setMaxUpdateAgeMillis(10000)
            setGranularity(Granularity.GRANULARITY_COARSE)
        }

        val currentLocationRequest = CurrentLocationRequest.Builder().apply {
            setMaxUpdateAgeMillis(10000)
            setGranularity(Granularity.GRANULARITY_COARSE)
        }
    }

    suspend fun getLocation(): Address? = withContext(ioContext) {
        try {
            getLastAddress() ?: getCurrentAddress()
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getLastAddress(): Address? = withContext(ioContext) {
        locationProviderClient.getLastLocation(lastLocationRequest.build()).await()?.let { lastLocation ->
            getAddressFromLocation(lastLocation).getOrNull()
        }
    }

    private suspend fun getCurrentAddress(): Address? = withContext(ioContext) {
        locationProviderClient.getCurrentLocation(currentLocationRequest.build(), null).await()?.let { currentLocation ->
            getAddressFromLocation(currentLocation).getOrNull()
        }
    }

    private suspend fun getAddressFromLocation(location: Location): Result<Address> = withContext(ioContext) {
        suspendCancellableCoroutine { continuation ->
            geocoder.getFromLocation(location.latitude, location.longitude, 1)?.let { addresses ->
                saveLocationInPreferences(addresses[0])
                continuation.resume(Result.success(addresses[0]))
            } ?: continuation.resume(Result.failure(Exception("No address found")))
        }
    }

    private fun saveLocationInPreferences(address: Address) {
        with(context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).edit()) {
            putFloat(Constants.LATITUDE, address.latitude.toFloat())
            putFloat(Constants.LONGITUDE, address.longitude.toFloat())
            putString(Constants.LOCALITY, address.locality ?: address.featureName)
            apply()
        }
    }
}