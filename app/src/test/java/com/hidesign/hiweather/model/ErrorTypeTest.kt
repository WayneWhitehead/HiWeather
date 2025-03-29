package com.hidesign.hiweather.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import com.hidesign.hiweather.data.model.ErrorType
import org.junit.Assert.assertEquals
import org.junit.Test

class ErrorTypeTest {

    @Test
    fun testPlacesError() {
        val errorType = ErrorType.PLACES_ERROR
        assertEquals("Unable to fetch places.", errorType.message)
        assertEquals(Icons.Outlined.WrongLocation, errorType.icon)
    }

    @Test
    fun testLocationError() {
        val errorType = ErrorType.LOCATION_ERROR
        assertEquals("Unable to fetch location.", errorType.message)
        assertEquals(Icons.Outlined.LocationDisabled, errorType.icon)
    }

    @Test
    fun testLocationPermissionError() {
        val errorType = ErrorType.LOCATION_PERMISSION_ERROR
        assertEquals("Location permission denied.", errorType.message)
        assertEquals(Icons.Outlined.LocationSearching, errorType.icon)
    }

    @Test
    fun testWeatherError() {
        val errorType = ErrorType.WEATHER_ERROR
        assertEquals("Unable to fetch weather.", errorType.message)
        assertEquals(Icons.Outlined.ErrorOutline, errorType.icon)
    }

    @Test
    fun testAllErrorTypes() {
        val errorTypes = ErrorType.entries.toTypedArray()
        assertEquals(4, errorTypes.size)
    }
}