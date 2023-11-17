package com.hidesign.hiweather.model

import org.junit.Assert
import org.junit.Test

class AirPollutionResponseTest {

    @Test
    fun constructor_air_pollution_response_success() {
        val airPollutionResponse = AirPollutionResponse(
            coord = Coord(lat = 12.3456, lon = 78.9012),
            list = listOf(DefaultAir(components = Components(co = 1.2, nh3 = 3.4, no = 5.6, no2 = 7.8, o3 = 9.0, pm10 = 11.2, pm25 = 13.4, so2 = 15.6), dt = 1661564800, main = Main(aqi = 100)))
        )

        Assert.assertNotNull(airPollutionResponse)
    }

    @Test
    fun constructor_default_air_success() {
        val defaultAir = DefaultAir(
            components = Components(co = 1.2, nh3 = 3.4, no = 5.6, no2 = 7.8, o3 = 9.0, pm10 = 11.2, pm25 = 13.4, so2 = 15.6),
            dt = 1661564800,
            main = Main(aqi = 100)
        )

        Assert.assertNotNull(defaultAir)
    }

    @Test
    fun constructor_main_success() {
        val main = Main(aqi = 100)

        Assert.assertNotNull(main)
    }

    @Test
    fun constructor_coord_success() {
        val coord = Coord(lat = 12.3456, lon = 78.9012)

        Assert.assertNotNull(coord)
    }

    @Test
    fun constructor_components_success() {
        val components = Components(co = 1.2, nh3 = 3.4, no = 5.6, no2 = 7.8, o3 = 9.0, pm10 = 11.2, pm25 = 13.4, so2 = 15.6)

        Assert.assertNotNull(components)
    }
}
