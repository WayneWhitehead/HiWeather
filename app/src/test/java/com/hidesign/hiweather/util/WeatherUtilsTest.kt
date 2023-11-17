package com.hidesign.hiweather.util

import android.content.Context
import android.content.res.Resources
import androidx.core.content.ContextCompat
import com.hidesign.hiweather.R
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test

class WeatherUtilsTest {

    @Test
    fun getMoonIcon_success() {
        val id = 0.5 // waxing gibbous
        val icon = WeatherUtils.getMoonIcon(id)

        Assert.assertEquals(R.drawable.new_moon, icon)
    }

    @Test
    fun getMoonIcon_out_of_range() {
        val id = 1.1 //out of range
        val icon = WeatherUtils.getMoonIcon(id)

        Assert.assertEquals(R.drawable.full_moon, icon)
    }

    @Test
    fun getWindDegreeText_success() {
        val deg = 360 // north
        val windDegreeText = WeatherUtils.getWindDegreeText(deg)
        Assert.assertEquals("N", windDegreeText)
    }

    @Test
    fun getWindDegreeText_out_of_range() {
        val deg = 361 // out of range
        val windDegreeText = WeatherUtils.getWindDegreeText(deg)
        Assert.assertEquals("?", windDegreeText)
    }

    @Test
    fun getWeatherIconUrl_success() {
        val icon = "10d" // sun
        val iconUrl = WeatherUtils.getWeatherIconUrl(icon)
        Assert.assertEquals("https://openweathermap.org/img/wn/10d@2x.png", iconUrl)
    }

    @Test
    fun getCurrentActiveSeriesItem_success() {
        val valueArray = intArrayOf(0, 25, 50, 75, 100, 125, 150)
        val current = 50F
        val activeSeriesItem = WeatherUtils.getCurrentActiveSeriesItem(valueArray, current)
        Assert.assertEquals(2, activeSeriesItem)
    }

    @Test
    fun getCurrentActiveSeriesItem_out_of_range() {
        val valueArray = intArrayOf(0, 25, 50, 75, 100, 125, 150)
        val current = 200F
        val activeSeriesItem = WeatherUtils.getCurrentActiveSeriesItem(valueArray, current)
        Assert.assertEquals(-1, activeSeriesItem)
    }

    @Test
    fun getAirQualityText_success() {
        val index = 3 // moderate
        val airQualityText = WeatherUtils.getAirQualityText(index)
        Assert.assertEquals("Moderate", airQualityText)
    }

    @Test
    fun getAirQualityText_out_of_range() {
        val index = 6 // out of range
        val airQualityText = WeatherUtils.getAirQualityText(index)
        Assert.assertEquals("Unknown", airQualityText)
    }

    @Test
    fun getAirQualityColour_success() {
        val context = mockk<Context>()
        val index = 3

        every { context.resources } returns mockk<Resources>()
        every { ContextCompat.getColor(context, index) } returns 2131099677
        every { context.resources.getColor(R.color.airIndex3) } returns R.color.airIndex3

        val airQualityColour = WeatherUtils.getAirQualityColour(index, context)
        Assert.assertEquals(R.color.airIndex3, airQualityColour)
    }

    @Test
    fun getAirQualityColour_out_of_range() {
        val context = mockk<Context>()
        val index = 5

        every { context.resources } returns mockk<Resources>()
        every { ContextCompat.getColor(context, index) } returns 2131099677
        every { context.resources.getColor(R.color.airIndex5) } returns R.color.airIndex5

        val airQualityColour = WeatherUtils.getAirQualityColour(index, context)
        Assert.assertEquals(R.color.airIndex5, airQualityColour)
    }
}
