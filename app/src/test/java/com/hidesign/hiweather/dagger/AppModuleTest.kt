package com.hidesign.hiweather.dagger

import android.app.Application
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class AppModuleTest {

    @Test
    fun shouldProvideContext() {
        val application = mockk<Application>()
        val context = AppModule.provideContext(application)
        every { application.applicationContext } returns context

        assertNotNull(context)
        assertEquals(application, context.applicationContext)
    }

    @Test
    fun shouldProvideGeocoder() {
        val context = mockk<Application>()
        val geocoder = AppModule.provideGeocoder(context)

        assertNotNull(geocoder)
    }

    @Test
    fun shouldProvideLocationProviderClient() {
        val context = mockk<Application>()
        val fusedLocationProviderClientMock: FusedLocationProviderClient = mockk()
        every { LocationServices.getFusedLocationProviderClient(context) } returns fusedLocationProviderClientMock

        val locationProviderClient = AppModule.provideLocationProviderClient(context)
        assertNotNull(locationProviderClient)
    }
}