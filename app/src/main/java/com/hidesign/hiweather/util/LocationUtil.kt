package com.hidesign.hiweather.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.ktx.performance
import com.hidesign.hiweather.views.WeatherActivity.Companion.uAddress
import kotlinx.coroutines.CompletableDeferred
import timber.log.Timber
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


object LocationUtil {
    private val myTrace = Firebase.performance.newTrace("FetchingUserLocation")
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
    suspend fun getLocation(activity: FragmentActivity?) {
        myTrace.start()
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, TimeUnit.SECONDS.toMillis(60))
            .setMaxUpdateDelayMillis(TimeUnit.MINUTES.toMillis(2))
            .build()
        val userAddress = CompletableDeferred<Address?>()
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(activity) { location: Location? ->
            if (location != null) {
                val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putFloat(Constants.latitude, location.latitude.toFloat())
                    putFloat(Constants.longitude, location.longitude.toFloat())
                    apply()
                }
                myTrace.stop()
                userAddress.complete(getAddress(activity, location.latitude, location.longitude))
            }
        }
        uAddress = userAddress.await()
    }

    private fun getAddress(context: Context, latitude: Double, longitude: Double): Address? {
        var addresses: List<Address>? = null
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
        } catch (e: IOException) {
            e.printStackTrace()
            Timber.tag("Tag").e("Error getting Street Address: ")
        }
        if (addresses != null) {
            return addresses[0]
        }
        return null
    }
}
