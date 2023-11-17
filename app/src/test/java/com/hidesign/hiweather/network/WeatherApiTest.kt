package com.hidesign.hiweather.network

import com.hidesign.hiweather.model.AirPollutionResponse
import com.hidesign.hiweather.model.OneCallResponse
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class WeatherApiTest {

    private val weatherApi = mockk<WeatherApi>()

    @Before
    fun setup() {
        every {
            runBlocking { weatherApi.getOneCall(any(), any(), apiKey = "INVALID_API_KEY", any(), any()) }
        } returns Response.error(401, "Unauthorized".toResponseBody(null))
        every {
            runBlocking { weatherApi.getOneCall(any(), any(), apiKey = "YOUR_API_KEY") }
        } returns Response.success(200, OneCallResponse())

        every {
            runBlocking { weatherApi.getAirPollution(any(), any(), apiKey = "INVALID_API_KEY") }
        } returns Response.error(401, "Unauthorized".toResponseBody(null))
        every {
            runBlocking { weatherApi.getAirPollution(any(), any(), apiKey = "YOUR_API_KEY") }
        } returns Response.success(200, AirPollutionResponse())
    }

    @Test
    fun getOneCall_returnsSuccessResponse() {
        runBlocking {
            val response = weatherApi.getOneCall(apiKey = "YOUR_API_KEY")

            assert(response.isSuccessful)
            assert(response.body() != null)
        }
    }

    @Test
    fun getAirPollution_returnsSuccessResponse() {
        runBlocking {
            val response = weatherApi.getAirPollution(apiKey = "YOUR_API_KEY")

            assert(response.isSuccessful)
            assert(response.body() != null)
        }
    }

    @Test
    fun getOneCall_withInvalidApiKey_returnsErrorResponse() {
        runBlocking {
            val response = weatherApi.getOneCall(apiKey = "INVALID_API_KEY")
            assert(!response.isSuccessful)
            assert(response.code() == 401)
        }
    }

    @Test
    fun getAirPollution_withInvalidApiKey_returnsErrorResponse() {
        runBlocking {
            val response = weatherApi.getAirPollution(apiKey = "INVALID_API_KEY")
            assert(!response.isSuccessful)
            assert(response.code() == 401)
        }
    }
}
