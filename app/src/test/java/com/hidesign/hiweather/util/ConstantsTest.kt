package com.hidesign.hiweather.util

import androidx.core.text.util.LocalePreferences
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ConstantsTest {

    @Before
    fun setUp() {
        mockkStatic(LocalePreferences::class)
    }

    @Test
    fun getUnit_metric() {
        every { LocalePreferences.getTemperatureUnit() } returns LocalePreferences.TemperatureUnit.CELSIUS
        val unit = Constants.getUnit()
        assertEquals("metric", unit)
    }

    @Test
    fun getUnit_imperial() {
        every { LocalePreferences.getTemperatureUnit() } returns LocalePreferences.TemperatureUnit.FAHRENHEIT
        val unit = Constants.getUnit()
        assertEquals("imperial", unit)
    }

    @Test
    fun getUnit_empty() {
        every { LocalePreferences.getTemperatureUnit() } returns ""
        val unit = Constants.getUnit()
        assertEquals("", unit)
    }
}