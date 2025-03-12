package com.hidesign.hiweather.model

import android.net.Uri
import com.google.gson.Gson
import com.hidesign.hiweather.data.model.OneCallResponse
import com.hidesign.hiweather.data.model.OneCallResponse.*
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class OneCallResponseTest {

    @Before
    fun setUp() {
        mockkStatic(Uri::class)
        every { Uri.encode(any()) } answers { firstArg() }
    }

    @Test
    fun constructor_one_call_response_success() {
        val oneCallResponse = OneCallResponse(
            current = Current(),
            daily = listOf(),
            hourly = listOf(),
            lat = 12.3456,
            lon = 78.9012,
            timezone = "Asia/Kolkata",
            timezoneOffset = 19800,
            alerts = listOf()
        )

        assertNotNull(oneCallResponse)
    }

    @Test
    fun constructor_current_success() {
        val current = Current(
            dt = 1661564800,
            clouds = 10,
            dewPoint = 20.0,
            feelsLike = 25.0,
            humidity = 60,
            pressure = 1013,
            sunrise = 600,
            sunset = 1800,
            temp = 30.0,
            uvi = 10.0,
            visibility = 10000,
            weather = listOf(),
            windDeg = 90,
            windGust = 10.0,
            windSpeed = 5.0
        )

        assertNotNull(current)
    }

    @Test
    fun constructor_weather_success() {
        val weather = Weather(
            description = "Cloudy",
            icon = "04d",
            weatherId = 803,
            main = "Clouds"
        )

        assertNotNull(weather)
    }

    @Test
    fun constructor_daily_success() {
        val daily = Daily(
            feelsLike = FeelsLike(day = 25.0, eve = 24.0, morn = 26.0, night = 23.0),
            moonPhase = 0.5,
            moonrise = 600,
            moonset = 1800,
            rain = 1.0,
            sunrise = 600,
            sunset = 1800,
            temp = Temp(day = 30.0, eve = 28.0, max = 32.0, min = 26.0, morn = 27.0, night = 25.0),
            summary = "Cloudy with a 30% chance of rain."
        )

        assertNotNull(daily)
    }

    @Test
    fun constructor_hourly_success() {
        val hourly = Hourly(
            feelsLike = 25.0,
            temp = 30.0,
            visibility = 10000,
        )

        assertNotNull(hourly)
    }

    @Test
    fun constructor_alerts_success() {
        val alerts = Alerts(
            senderName = "National Weather Service",
            event = "Tornado Warning",
            start = 1661564800,
            end = 1661568400,
            description = "A tornado warning has been issued for the following counties:..."
        )

        assertNotNull(alerts)
    }

    @Test
    fun testCurrentToJson() {
        val current = Current()
        val json = current.toJson()
        val expectedJson = Uri.encode(Gson().toJson(current))
        assertEquals(expectedJson, json)
    }

    @Test
    fun testDailyToJson() {
        val daily = Daily()
        val json = daily.toJson()
        val expectedJson = Uri.encode(Gson().toJson(daily))
        assertEquals(expectedJson, json)
    }

    @Test
    fun testHourlyToJson() {
        val hourly = Hourly(0.0, 0.0, 0)
        val json = hourly.toJson()
        val expectedJson = Uri.encode(Gson().toJson(hourly))
        assertEquals(expectedJson, json)
    }
}