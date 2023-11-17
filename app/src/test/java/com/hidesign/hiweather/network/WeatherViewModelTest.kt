package com.hidesign.hiweather.network
import android.location.Address
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hidesign.hiweather.model.AirPollutionResponse
import com.hidesign.hiweather.model.OneCallResponse
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class WeatherViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val weatherRepository = mockk<WeatherRepository>()
    private val weatherViewModel = WeatherViewModel(weatherRepository)
    private val address = mockk<Address> {
        every { hasLatitude() } returns true
        every { latitude } returns 0.0
        every { hasLongitude() } returns true
        every { longitude } returns 0.0
    }

    @Test
    fun getOneCall_SuccessfulResponse() {
        every { runBlocking { weatherRepository.getWeather(any(), any(), any()) } } returns Response.success(OneCallResponse())

        runBlocking {
            weatherViewModel.getOneCallWeather(address, "metric")
            assert(weatherViewModel.oneCallResponse.value != null)
            assert(weatherViewModel.uiState.value == NetworkStatus.SUCCESS)
        }
    }

    @Test
    fun getOneCall_SuccessfulResponse_NullBody() {
        every { runBlocking { weatherRepository.getWeather(any(), any(), any()) } } returns Response.success(null)

        runBlocking {
            weatherViewModel.getOneCallWeather(address, "metric")
            assert(weatherViewModel.uiState.value == NetworkStatus.ERROR)
        }
    }

    @Test
    fun getAirPollution_SuccessfulResponse() {
        every { runBlocking { weatherRepository.getAirPollution(any(), any()) } } returns Response.success(AirPollutionResponse())

        runBlocking {
            weatherViewModel.getAirPollution(address)
            assert(weatherViewModel.airPollutionResponse.value != null)
            assert(weatherViewModel.uiState.value == NetworkStatus.SUCCESS)
        }
    }

    @Test
    fun getAirPollution_SuccessfulResponse_NullBody() {
        every { runBlocking { weatherRepository.getAirPollution(any(), any()) } } returns Response.success(null)

        runBlocking {
            weatherViewModel.getAirPollution(address)
            assert(weatherViewModel.uiState.value == NetworkStatus.ERROR)
        }
    }

    @Test
    fun updateUIState_withNullStatus_doesNothing() {
        weatherViewModel.updateUIState(NetworkStatus.LOADING)
        assert(weatherViewModel.uiState.value == NetworkStatus.LOADING)
    }
}
