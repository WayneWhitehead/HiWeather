package com.hidesign.hiweather.util

import android.content.Context
import android.content.res.Resources
import androidx.core.content.ContextCompat
import com.hidesign.hiweather.R
import com.hidesign.hiweather.data.model.AirPollutionResponse.Components
import com.hidesign.hiweather.data.model.OneCallResponse.Temp
import com.hidesign.hiweather.data.model.OneCallResponse.FeelsLike
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test

class WeatherUtilTest {

    @Test
    fun getMoonIcon_allCases() {
        val moonPhases = mapOf(
            0.05 to R.drawable.full_moon,
            0.15 to R.drawable.waxing_moon_2,
            0.25 to R.drawable.first_quarter_moon,
            0.35 to R.drawable.waxing_moon,
            0.5 to R.drawable.new_moon,
            0.65 to R.drawable.waning_moon,
            0.75 to R.drawable.last_quarter_moon,
            0.85 to R.drawable.waning_moon_2,
            0.95 to R.drawable.full_moon,
            1.1 to R.drawable.full_moon
        )

        for ((id, expectedIcon) in moonPhases) {
            val icon = WeatherUtil.getMoonIcon(id)
            Assert.assertEquals(expectedIcon, icon)
        }
    }

    @Test
    fun getWindDegreeText_allCases() {
        val windDirections = mapOf(
            0 to "N",
            12 to "NNE",
            34 to "NE",
            56 to "ENE",
            78 to "E",
            101 to "ESE",
            123 to "SE",
            145 to "SSE",
            167 to "S",
            191 to "SSW",
            213 to "SW",
            235 to "WSW",
            257 to "W",
            281 to "WNW",
            303 to "NW",
            325 to "NNW",
            347 to "N",
            361 to "?"
        )

        for ((deg, expectedDirection) in windDirections) {
            val windDegreeText = WeatherUtil.getWindDegreeText(deg)
            Assert.assertEquals(expectedDirection, windDegreeText)
        }
    }

    @Test
    fun getWeatherIconUrl_success() {
        val icon = "10d" // sun
        val iconUrl = WeatherUtil.getWeatherIconUrl(icon)
        Assert.assertEquals("https://openweathermap.org/img/wn/10d@2x.png", iconUrl)
    }

    @Test
    fun getComponentList_success() {
        val components = Components(
            co = 1.0,
            nh3 = 2.0,
            no = 3.0,
            no2 = 4.0,
            o3 = 5.0,
            pm10 = 6.0,
            pm25 = 7.0,
            so2 = 8.0
        )

        val expectedList = listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0)
        val componentList = WeatherUtil.getComponentList(components)

        Assert.assertEquals(expectedList, componentList)
    }

    @Test
    fun getCurrentActiveSeriesItem_success() {
        val valueArray = intArrayOf(0, 25, 50, 75, 100, 125, 150)
        val current = 50F
        val activeSeriesItem = WeatherUtil.getCurrentActiveSeriesItem(valueArray, current)
        Assert.assertEquals(2, activeSeriesItem)
    }

    @Test
    fun getCurrentActiveSeriesItem_out_of_range() {
        val valueArray = intArrayOf(0, 25, 50, 75, 100, 125, 150)
        val current = 200F
        val activeSeriesItem = WeatherUtil.getCurrentActiveSeriesItem(valueArray, current)
        Assert.assertEquals(-1, activeSeriesItem)
    }

    @Test
    fun getAirQualityText_allCases() {
        val airQualityTexts = mapOf(
            1 to "Good",
            2 to "Fair",
            3 to "Moderate",
            4 to "Poor",
            5 to "Very Poor",
            6 to "Unknown" // out of range
        )

        for ((index, expectedText) in airQualityTexts) {
            val airQualityText = WeatherUtil.getAirQualityText(index)
            Assert.assertEquals(expectedText, airQualityText)
        }
    }

    @Test
    fun getTempOfDay_allCases() {
        val temp = Temp(night = 10.0, morn = 20.0, day = 30.0, eve = 40.0)
        val tempOfDay = mapOf(
            3 to 10,
            9 to 20,
            15 to 30,
            21 to 40,
            25 to 30
        )

        for ((currentHour, expectedTemp) in tempOfDay) {
            val result = WeatherUtil.getTempOfDay(currentHour, temp)
            Assert.assertEquals(expectedTemp, result)
        }
    }

    @Test
    fun getFeelsLikeOfDay_allCases() {
        val feelsLike = FeelsLike(night = 10.0, morn = 20.0, day = 30.0, eve = 40.0)
        val feelsLikeOfDay = mapOf(
            3 to 10,
            9 to 20,
            15 to 30,
            21 to 40,
            25 to 30
        )

        for ((currentHour, expectedFeelsLike) in feelsLikeOfDay) {
            val result = WeatherUtil.getFeelsLikeOfDay(currentHour, feelsLike)
            Assert.assertEquals(expectedFeelsLike, result)
        }
    }

    @Test
    fun getAirQualityColour_allCases() {
        val context = mockk<Context>()
        every { ContextCompat.getColor(context, R.color.airIndex1) } returns 0xFF0000
        every { ContextCompat.getColor(context, R.color.airIndex2) } returns 0x00FF00
        every { ContextCompat.getColor(context, R.color.airIndex3) } returns 0x0000FF
        every { ContextCompat.getColor(context, R.color.airIndex4) } returns 0xFFFF00
        every { ContextCompat.getColor(context, R.color.airIndex5) } returns 0xFF00FF

        val airQualityColours = mapOf(
            1 to 0xFF0000,
            2 to 0x00FF00,
            3 to 0x0000FF,
            4 to 0xFFFF00,
            5 to 0xFF00FF,
            6 to 0xFF00FF // out of range
        )

        for ((index, expectedColour) in airQualityColours) {
            val colour = WeatherUtil.getAirQualityColour(index, context)
            Assert.assertEquals(expectedColour, colour)
        }
    }
}
