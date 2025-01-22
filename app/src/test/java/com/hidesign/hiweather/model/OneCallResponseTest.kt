package com.hidesign.hiweather.model

import com.hidesign.hiweather.data.model.*
import org.junit.Assert
import org.junit.Test

class OneCallResponseTest {

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

        Assert.assertNotNull(oneCallResponse)
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

        Assert.assertNotNull(current)
    }

    @Test
    fun constructor_weather_success() {
        val weather = Weather(
            description = "Cloudy",
            icon = "04d",
            weatherId = 803,
            main = "Clouds"
        )

        Assert.assertNotNull(weather)
    }

    @Test
    fun constructor_daily_success() {
        val daily = Daily(
            clouds = 10,
            dewPoint = 20.0,
            dt = 1661564800,
            feelsLike = FeelsLike(day = 25.0, eve = 24.0, morn = 26.0, night = 23.0),
            humidity = 60,
            moonPhase = 0.5,
            moonrise = 600,
            moonset = 1800,
            pop = 0.3,
            pressure = 1013,
            rain = 1.0,
            sunrise = 600,
            sunset = 1800,
            temp = Temp(day = 30.0, eve = 28.0, max = 32.0, min = 26.0, morn = 27.0, night = 25.0),
            uvi = 10.0,
            weather = listOf(),
            windDeg = 90,
            windGust = 10.0,
            windSpeed = 5.0,
            summary = "Cloudy with a 30% chance of rain."
        )

        Assert.assertNotNull(daily)
    }

    @Test
    fun constructor_hourly_success() {
        val hourly = Hourly(
            clouds = 10,
            dewPoint = 20.0,
            dt = 1661564800,
            feelsLike = 25.0,
            humidity = 60,
            pop = 0.3,
            pressure = 1013,
            temp = 30.0,
            uvi = 10.0,
            visibility = 10000,
            weather = listOf(),
            windDeg = 90,
            windGust = 10.0,
            windSpeed = 5.0
        )

        Assert.assertNotNull(hourly)
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

        Assert.assertNotNull(alerts)
    }
}
