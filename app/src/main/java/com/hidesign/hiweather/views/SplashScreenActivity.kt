package com.hidesign.hiweather.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.hidesign.hiweather.R
import com.hidesign.hiweather.util.LocationUtil.getAddress

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private var googleApiClient: GoogleApiClient? = null
    private var permissionsToRequest: ArrayList<String>? = null
    private val permissionsRejected = ArrayList<String>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (googleApiClient != null) {
            googleApiClient!!.connect()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    location
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    location
                } else -> {
                    finish()
                }
            }
        }
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
        googleApiClient = GoogleApiClient.Builder(this).addApi(LocationServices.API).build()
    }

    private fun showMain() {
        val i = Intent(this@SplashScreenActivity, WeatherActivity::class.java)
        startActivity(i)
        overridePendingTransition(R.anim.slide_up, R.anim.slide_up_out)
        finish()
    }

    private val location: Unit
        @SuppressLint("MissingPermission")
        get() {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location: Location? ->
                if (location != null) {
                    uLocation = location
                    showMain()
                    uAddress = getAddress(this, uLocation!!.latitude, uLocation!!.longitude)
                    Log.e("Getting Location.....",
                        uLocation!!.latitude.toString() + "," + uLocation!!.longitude + " .... Address: " + uAddress)
                } else {
                    showMain()
                }
            }
        }

    private fun hasPermission(permission: String): Boolean {
        return checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1011) {
            for (perm in permissionsToRequest!!) {
                if (hasPermission(perm)) {
                    permissionsRejected.add(perm)
                }
            }
            if (permissionsRejected.size > 0) {
                if (shouldShowRequestPermissionRationale(permissionsRejected[0])) {
                    AlertDialog.Builder(this)
                        .setMessage("These permissions are mandatory to get your location.")
                        .setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                            requestPermissions(permissionsRejected.toTypedArray(),
                                1011)
                        }
                        .setNegativeButton("Cancel", null).create().show()
                    location
                    if (uLocation != null) {
                        showMain()
                    }
                }
            } else {
                if (googleApiClient != null) {
                    googleApiClient!!.connect()
                    location
                    showMain()
                }
                return
            }
            location
        }
    }

    companion object {
        @JvmField
        var uLocation: Location? = null
        @JvmField
        var uAddress: Address? = null
    }
}