package com.hidesign.hiweather.util

import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.io.IOException

class LocationUtilTest {

    private lateinit var locationUtil: LocationUtil
    private val fusedLocationProviderClientMock: FusedLocationProviderClient = mockk()
    private val geocoderMock: Geocoder = mockk()
    private val addressCallbackMock: LocationUtil.AddressCallback = mockk()
    private val addressMock = mockk<Address>()
    private val locationMock = mockk<Location>().apply {
        coEvery { latitude } returns 0.0
        coEvery { longitude } returns 0.0
    }

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        locationUtil = LocationUtil(fusedLocationProviderClientMock, geocoderMock)
        coEvery { addressCallbackMock.onSuccess(addressMock) } returns Unit
        coEvery { addressCallbackMock.onFailure() } returns Unit
    }

    @Test
    fun `getLastLocation should return OnSuccess`() {
        every { fusedLocationProviderClientMock.lastLocation } returns mockk {
            every { addOnCompleteListener(any()) } answers {
                firstArg<OnCompleteListener<Location>>().onComplete(mockk {
                    every { isSuccessful } returns true
                    every { result } returns locationMock
                })
                mockk<Task<Location>>()
            }
        }

        every { geocoderMock.getFromLocation(any(), any(), any()) } returns listOf(addressMock)
        locationUtil.getLastLocation(addressCallbackMock)
        verify { addressCallbackMock.onSuccess(addressMock) }
    }

    @Test
    fun `getLastLocation should call getCurrentLocation which should return OnSuccess`() {
        every { fusedLocationProviderClientMock.lastLocation } returns mockk {
            every { addOnCompleteListener(any()) } answers {
                firstArg<OnCompleteListener<Location>>().onComplete(mockk {
                    every { isSuccessful } returns false
                    every { result } returns null
                })
                mockk<Task<Location>>()
            }
        }

        every { fusedLocationProviderClientMock.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null) } returns mockk {
            every { addOnCompleteListener(any()) } answers {
                firstArg<OnCompleteListener<Location>>().onComplete(mockk {
                    every { isSuccessful } returns true
                    every { result } returns locationMock
                })
                mockk<Task<Location>>()
            }
        }

        every { geocoderMock.getFromLocation(any(), any(), any()) } returns listOf(addressMock)
        locationUtil.getLastLocation(addressCallbackMock)
        verify { addressCallbackMock.onSuccess(addressMock) }
    }

    @Test
    fun `getLastLocation should call getCurrentLocation which should return OnFailure`() {
        every { fusedLocationProviderClientMock.lastLocation } returns mockk {
            every { addOnCompleteListener(any()) } answers {
                firstArg<OnCompleteListener<Location>>().onComplete(mockk {
                    every { isSuccessful } returns false
                    every { result } returns null
                })
                mockk<Task<Location>>()
            }
        }

        every { fusedLocationProviderClientMock.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null) } returns mockk {
            every { addOnCompleteListener(any()) } answers {
                firstArg<OnCompleteListener<Location>>().onComplete(mockk {
                    every { isSuccessful } returns false
                    every { result } returns null
                })
                mockk<Task<Location>>()
            }
        }

        locationUtil.getLastLocation(addressCallbackMock)
        verify { addressCallbackMock.onFailure() }
    }

    @Test
    fun `handleLocation should call callback with failure`() {
        coEvery { geocoderMock.getFromLocation(any(), any(), any()) } returns listOf(null)

        locationUtil.handleLocation(locationMock, addressCallbackMock)
        coVerify(exactly = 1) { addressCallbackMock.onFailure() }
    }

    @Test
    fun `handleLocation should call callback with failure message if error`() {
        coEvery { geocoderMock.getFromLocation(any(), any(), any()) } throws IOException()

        locationUtil.handleLocation(locationMock, addressCallbackMock)
        verify(exactly = 1) { addressCallbackMock.onFailure() }
    }
}