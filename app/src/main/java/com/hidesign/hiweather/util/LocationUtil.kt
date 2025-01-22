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
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

class LocationUtil @Inject constructor(
    private val context: Context,
    @Named("io") private val ioContext: CoroutineContext,
    private val locationProviderClient: FusedLocationProviderClient,
    private val geocoder: Geocoder
) {
    private val coroutineScope = CoroutineScope(ioContext) + Job()

    @SuppressLint("MissingPermission")
    suspend fun getLocation(): Address = suspendCancellableCoroutine { continuation ->
        val lastLocationRequest = LastLocationRequest.Builder().apply {
            setMaxUpdateAgeMillis(10000)
            setGranularity(Granularity.GRANULARITY_COARSE)
        }

        coroutineScope.launch {
            try {
                locationProviderClient.getLastLocation(lastLocationRequest.build()).await()?.let {
                    continuation.resumeWith(getAddressFromLocation(it))
                } ?: run {
                    val currentLocationRequest = CurrentLocationRequest.Builder().apply {
                        setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                        setMaxUpdateAgeMillis(10000)
                        setGranularity(Granularity.GRANULARITY_COARSE)
                    }
                    locationProviderClient.getCurrentLocation(currentLocationRequest.build(), null).await()?.let {
                        continuation.resumeWith(getAddressFromLocation(it))
                    }
                }
            } catch (e: Exception) {
                continuation.resumeWith(Result.failure(e))
            }
        }
    }

    private suspend fun getAddressFromLocation(location: Location): Result<Address> {
        return withContext(ioContext) {
            val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)?.first()!!
            saveLocationInPreferences(address)
            Result.success(address)
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