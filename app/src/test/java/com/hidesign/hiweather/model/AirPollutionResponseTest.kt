package com.hidesign.hiweather.model

import android.net.Uri
import com.google.gson.Gson
import com.hidesign.hiweather.data.model.AirPollutionResponse
import com.hidesign.hiweather.data.model.AirPollutionResponse.*
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class AirPollutionResponseTest {

    @Before
    fun setUp() {
        mockkStatic(Uri::class)
        every { Uri.encode(any()) } answers { firstArg() }
    }

    @Test
    fun constructor_air_pollution_response_success() {
        val airPollutionResponse = AirPollutionResponse(
            coord = Coord(lat = 12.3456, lon = 78.9012),
            list = listOf(DefaultAir(components = Components(), dt = 1661564800, main = Main(aqi = 100)))
        )

        assertNotNull(airPollutionResponse)
    }

    @Test
    fun constructor_default_air_success() {
        val defaultAir = DefaultAir(
            components = Components(),
            dt = 1661564800,
            main = Main(aqi = 100)
        )

        assertNotNull(defaultAir)
    }

    @Test
    fun constructor_main_success() {
        val main = Main(aqi = 100)

        assertNotNull(main)
    }

    @Test
    fun constructor_coord_success() {
        val coord = Coord(lat = 12.3456, lon = 78.9012)

        assertNotNull(coord)
    }

    @Test
    fun constructor_components_success() {
        val components = Components()

        assertNotNull(components)
    }

    @Test
    fun testComponentsToJson() {
        val components = Components()
        val json = components.toJson()
        val expectedJson = Uri.encode(Gson().toJson(components))
        assertEquals(expectedJson, json)
    }
}