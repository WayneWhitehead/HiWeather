package com.hidesign.hiweather.dagger

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AppModuleTest {

    private lateinit var application: Application
    private lateinit var context: Context
    private lateinit var packageManager: PackageManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Before
    fun setUp() {
        application = mockk()
        context = mockk()
        packageManager = mockk()
        fusedLocationProviderClient = mockk()

        every { application.packageManager } returns packageManager
        every { application.applicationContext } returns application
        every { context.applicationContext } returns application
        every { LocationServices.getFusedLocationProviderClient(context) } returns fusedLocationProviderClient
    }

    @Test
    fun provideGeocoder_returnsGeocoderInstance() {
        val context = AppModule.provideContext(application)
        every { context.packageName } returns "com.hidesign.hiweather"
        val geocoder = AppModule.provideGeocoder(context)
        assertNotNull(geocoder)
        assertEquals(application, context)
        assertEquals(Geocoder::class.java, geocoder::class.java)
    }

    @Test
    fun provideLocationProviderClient_returnsFusedLocationProviderClientInstance() {
        val locationProviderClient = AppModule.provideLocationProviderClient(context)
        assertNotNull(locationProviderClient)
        assertEquals(fusedLocationProviderClient, locationProviderClient)
    }

    @Test
    fun provideIOContext_returnsIOCoroutineContext() {
        val ioContext: CoroutineContext = AppModule.provideIOContext()
        assertEquals(Dispatchers.IO, ioContext)
    }

    @Test
    fun provideMainContext_returnsMainCoroutineContext() {
        val mainContext: CoroutineContext = AppModule.provideMainContext()
        assertEquals(Dispatchers.Main, mainContext)
    }
}