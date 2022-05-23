package com.hidesign.hiweather.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.io.IOException
import java.util.*

object LocationUtil {

    fun getAddress(context: Context, latitude: Double, longitude: Double): Address {
        var addresses: List<Address>? = null
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("Tag", "Error getting Street Address: ")
        }
        if (addresses == null) {
            return Address(Locale.getDefault())
        }
        return addresses[0]
    }
}