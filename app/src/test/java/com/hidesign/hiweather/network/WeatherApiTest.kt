package com.hidesign.hiweather.network

import com.hidesign.hiweather.data.model.AirPollutionResponse
import com.hidesign.hiweather.data.model.OneCallResponse
import com.hidesign.hiweather.domain.repository.WeatherApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class WeatherApiTest {

    private val weatherApi = mockk<WeatherApi>()

    @Before
    fun setup() {
        coEvery {
            weatherApi.getOneCall(any(), any(), any(), any())
        } returns Response.success(OneCallResponse())

        coEvery {
            weatherApi.getAirPollution(any(), any())
        } returns Response.success(AirPollutionResponse())
    }

    @Test
    fun getOneCall_returnsSuccessResponse() = runTest {
        val response = weatherApi.getOneCall(0.0, 0.0, "minutely", "metric")
        assert(response.isSuccessful)
        assertNotNull(response.body())

    }

    @Test
    fun getAirPollution_returnsSuccessResponse() = runTest {
        val response = weatherApi.getAirPollution(0.0, 0.0)
        assert(response.isSuccessful)
        assertNotNull(response.body())
    }

    @Test
    fun getOneCall_withInvalidApiKey_returnsErrorResponse() {
        coEvery {
            weatherApi.getOneCall(any(), any(), any(), any())
        } returns Response.error(401, "Unauthorized".toResponseBody(null))

        runTest {
            val response = weatherApi.getOneCall(0.0, 0.0, "minutely", "metric")
            assert(!response.isSuccessful)
            assertEquals(401, response.code())
        }
    }

    @Test
    fun getAirPollution_withInvalidApiKey_returnsErrorResponse() {
        coEvery {
            weatherApi.getAirPollution(any(), any())
        } returns Response.error(401, "Unauthorized".toResponseBody(null))

        runTest {
            val response = weatherApi.getAirPollution(0.0, 0.0)
            assert(!response.isSuccessful)
            assertEquals(401, response.code())
        }
    }
}