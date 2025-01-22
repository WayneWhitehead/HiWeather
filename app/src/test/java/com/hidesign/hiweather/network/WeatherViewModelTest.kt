package com.hidesign.hiweather.network
import android.location.Address
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hidesign.hiweather.data.repository.WeatherRepositoryImpl
import com.hidesign.hiweather.data.model.AirPollutionResponse
import com.hidesign.hiweather.data.model.OneCallResponse
import com.hidesign.hiweather.presentation.WeatherViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class WeatherViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val weatherRepositoryImpl = mockk<WeatherRepositoryImpl>()
    private val weatherViewModel = WeatherViewModel(weatherRepositoryImpl)
    private val address = mockk<Address> {
        every { hasLatitude() } returns true
        every { latitude } returns 0.0
        every { hasLongitude() } returns true
        every { longitude } returns 0.0
    }

    @Test
    fun getOneCall_SuccessfulResponse() {
        every { runBlocking { weatherRepositoryImpl.getOneCall(any(), any(), any()) } } returns Response.success(
            OneCallResponse()
        )

        runBlocking {
            weatherViewModel.getOneCallWeather(address, "metric")
            assert(weatherViewModel.oneCallResponse.value != null)
            assert(weatherViewModel.uiState.value == NetworkStatus.SUCCESS)
        }
    }

    @Test
    fun getOneCall_SuccessfulResponse_NullBody() {
        every { runBlocking { weatherRepositoryImpl.getOneCall(any(), any(), any()) } } returns Response.success(null)

        runBlocking {
            weatherViewModel.getOneCallWeather(address, "metric")
            assert(weatherViewModel.uiState.value == NetworkStatus.ERROR)
        }
    }

    @Test
    fun getAirPollution_SuccessfulResponse() {
        every { runBlocking { weatherRepositoryImpl.getAirPollution(any(), any()) } } returns Response.success(
            AirPollutionResponse()
        )

        runBlocking {
            weatherViewModel.getAirPollution(address)
            assert(weatherViewModel.airPollutionResponse.value != null)
            assert(weatherViewModel.uiState.value == NetworkStatus.SUCCESS)
        }
    }

    @Test
    fun getAirPollution_SuccessfulResponse_NullBody() {
        every { runBlocking { weatherRepositoryImpl.getAirPollution(any(), any()) } } returns Response.success(null)

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
