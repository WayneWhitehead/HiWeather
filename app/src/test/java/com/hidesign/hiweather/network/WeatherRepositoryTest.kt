package com.hidesign.hiweather.network

import com.hidesign.hiweather.model.AirPollutionResponse
import com.hidesign.hiweather.model.OneCallResponse
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class WeatherRepositoryTest {

    private val weatherApi = mockk<WeatherApi>()
    private val weatherRepository = WeatherRepository(weatherApi, "YOUR_API_KEY")

    @Before
    fun setup() {
        every {
            runBlocking {
                weatherApi.getOneCall(
                    lat = 0.0,
                    lon = 0.0,
                    apiKey = "YOUR_API_KEY"
                )
            }
        } returns Response.success(OneCallResponse())
        every {
            runBlocking {
                weatherApi.getAirPollution(
                    lat = 0.0,
                    lon = 0.0,
                    apiKey = "YOUR_API_KEY"
                )
            }
        } returns Response.success(
            AirPollutionResponse()
        )
    }
    @Test
    fun getWeather_withValidParameters_returnsSuccessResponse() {
        runBlocking {
            val response = weatherRepository.getWeather(lat = 0.0, lon = 0.0, unit = "metric")

            assert(response.isSuccessful)
            assert(response.body() != null)
        }
    }

    @Test
    fun getAirPollution_withValidParameters_returnsSuccessResponse() {
        runBlocking {
            val response = weatherRepository.getAirPollution(lat = 0.0, lon = 0.0)

            assert(response.isSuccessful())
            assert(response.body() != null)
        }
    }

    @Test
    fun getWeather_withInvalidApiKey_throwsException() {
        runBlocking {
            every { runBlocking { weatherApi.getOneCall(lat = 0.0, lon = 0.0, apiKey = "YOUR_API_KEY") } } returns Response.error(401, "Unauthorized".toResponseBody(null))

            assertThrows(Exception::class.java) { runBlocking { weatherRepository.getWeather(lat = 0.0, lon = 0.0, unit = "metric") } }
        }
    }

    @Test
    fun getAirPollution_withInvalidApiKey_throwsException() {
        runBlocking {
            every { runBlocking { weatherApi.getAirPollution(lat = 0.0, lon = 0.0, apiKey = "YOUR_API_KEY") } } returns Response.error(401, "Unauthorized".toResponseBody(null))

            assertThrows(Exception::class.java) { runBlocking { weatherRepository.getAirPollution(lat = 0.0, lon = 0.0) } }
        }
    }
}
