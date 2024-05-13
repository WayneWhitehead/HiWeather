package com.hidesign.hiweather

import android.Manifest
import android.content.SharedPreferences
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.libraries.places.api.Places
import com.hidesign.hiweather.model.OneCallResponse
import com.hidesign.hiweather.model.UIStatus
import com.hidesign.hiweather.network.WeatherViewModel
import com.hidesign.hiweather.util.LocationUtil
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class MainActivityTest {


    private val locationUtilMock = mockkClass(LocationUtil::class)
    private val fusedLocationProviderClientMock = mockkClass(FusedLocationProviderClient::class)
    private val placesClientMock = mockkClass(Places::class)
    private val weatherViewModelMock = mockkClass(WeatherViewModel::class)
    private val sharedPreferencesMock = mockkClass(SharedPreferences::class)
    private val sharedPreferencesEditorMock = mockkClass(SharedPreferences.Editor::class)

    @Before
    fun setup() {
        //every { locationUtilMock.getLocation() } returns mockk<Address>()
        every { weatherViewModelMock.uiState } returns MutableLiveData(UIStatus.Loading)
        every { weatherViewModelMock.oneCallResponse } returns MutableLiveData(OneCallResponse())
        every { weatherViewModelMock.airPollutionResponse } returns MutableLiveData(null)
        every { sharedPreferencesMock.edit() } returns sharedPreferencesEditorMock
        every { sharedPreferencesEditorMock.apply() } returns Unit
    }

    @Test
    fun testOnCreate() {

        verify { locationPermissionsLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)) }
    }

    private val locationPermissionsLauncher: ActivityResultLauncher<Array<String>> =
        mockk(relaxed = true)
}
