package com.hidesign.hiweather.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CompletableDeferred
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


object LocationUtil {
    private lateinit var locationRequest: LocationRequest
    private const val REQUEST_PERMISSION = 1
    private val PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)

    fun verifyPermissions(activity: AppCompatActivity): Boolean {
        val permissionWrite =
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
        val permissionRead =
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        return if (permissionWrite != PackageManager.PERMISSION_GRANTED || permissionRead != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(activity, PERMISSIONS, REQUEST_PERMISSION)
            false
        } else {
            true
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getLocation(activity: AppCompatActivity?): Address? {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity!!)
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = LocationRequest.create().priority
        }
        val userAddress = CompletableDeferred<Address?>()
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(activity) { location: Location? ->
            if (location != null) {
                val mPrefs: SharedPreferences =
                    activity.getSharedPreferences(Constants.userPreferences, MODE_PRIVATE)
                val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
                prefsEditor.putFloat("userLatitude", location.latitude.toFloat())
                prefsEditor.putFloat("userLongitude", location.longitude.toFloat())
                prefsEditor.apply()
                userAddress.complete(getAddress(activity, location.latitude, location.longitude))
            }
        }
        return userAddress.await()
    }

    private fun getAddress(context: Context, latitude: Double, longitude: Double): Address? {
        var addresses: List<Address>? = null
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("Tag", "Error getting Street Address: ")
        }
        if (addresses != null) {
            return addresses[0]
        }
        return null
    }
}
