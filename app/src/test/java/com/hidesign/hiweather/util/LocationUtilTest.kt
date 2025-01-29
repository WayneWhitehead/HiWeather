package com.hidesign.hiweather.util

import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class LocationUtilTest {

    @get:Rule
    val globalTimeout: Timeout = Timeout(10, TimeUnit.SECONDS)
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val context: Context = mockk()
    private val locationProviderClient: FusedLocationProviderClient = mockk()
    private val geocoder: Geocoder = mockk()
    private val sharedPreferences: SharedPreferences = mockk()
    private lateinit var locationUtil: LocationUtil

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        locationUtil = LocationUtil(testDispatcher, context, locationProviderClient, geocoder)
        every { context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE) } returns sharedPreferences
    }

    @Test
    fun getLocation_returnsAddress() = runTest(testDispatcher) {
        val location = mockk<Location>()
        val address = mockk<Address>()
        coEvery { locationProviderClient.getLastLocation(any()).await() } returns location
        coEvery { geocoder.getFromLocation(location.latitude, location.longitude, 1) } returns listOf(address)

        val result = locationUtil.getLocation()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(address, result)
    }

    @Test
    fun getLocation_returnsNullOnFailure() = runTest(testDispatcher) {
        coEvery { locationProviderClient.getLastLocation(any()).await() } returns null
        coEvery { locationProviderClient.getCurrentLocation(mockk<CurrentLocationRequest>(), any()).await() } returns null

        val result = locationUtil.getLocation()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(null, result)
    }

    @Test
    fun getLocation_handlesGeocoderIOException() = runTest(testDispatcher) {
        val location = mockk<Location>()
        coEvery { locationProviderClient.getLastLocation(any()).await() } returns location
        coEvery { geocoder.getFromLocation(location.latitude, location.longitude, 1) } throws IOException()

        val result = locationUtil.getLocation()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(null, result)
    }
}