package com.hidesign.hiweather.network

import android.location.Address
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hidesign.hiweather.data.model.AirPollutionResponse
import com.hidesign.hiweather.data.model.ErrorType
import com.hidesign.hiweather.data.model.OneCallResponse
import com.hidesign.hiweather.domain.usecase.GetAirPollutionUseCase
import com.hidesign.hiweather.domain.usecase.GetOneCallUseCase
import com.hidesign.hiweather.presentation.WeatherViewModel
import com.hidesign.hiweather.util.LocationUtil
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val getOneCallUseCase: GetOneCallUseCase = mockk()
    private val getAirPollutionUseCase: GetAirPollutionUseCase = mockk()
    private val locationUtil: LocationUtil = mockk()
    private lateinit var viewModel: WeatherViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = WeatherViewModel(testDispatcher, testDispatcher, getOneCallUseCase, getAirPollutionUseCase, locationUtil)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchWeather_ThrowsError() = runTest(testDispatcher) {
        val address = mockk<Address>()
        coEvery { locationUtil.getLocation() } returns address
        coEvery { getOneCallUseCase(address) } throws Exception()
        coEvery { getAirPollutionUseCase(address) } throws Exception()

        viewModel.fetchWeather()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.first { it.errorType != null }
        coVerify(exactly = 1) { locationUtil.getLocation() }
        coVerify(exactly = 1) { getOneCallUseCase(address) }
        coVerify(exactly = 0) { getAirPollutionUseCase(address) }
        assertEquals(ErrorType.LOCATION_ERROR, state.errorType)
    }

    @Test
    fun fetchWeather_withValidLocation_updatesState() = runTest(testDispatcher) {
        val address = mockk<Address>()
        val oneCallResponse = mockk<OneCallResponse>()
        val airPollutionResponse = mockk<AirPollutionResponse>()

        coEvery { locationUtil.getLocation() } returns address
        coEvery { getOneCallUseCase(address) } returns flowOf(Result.success(oneCallResponse))
        coEvery { getAirPollutionUseCase(address) } returns flowOf(Result.success(airPollutionResponse))

        viewModel.fetchWeather()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.first { it.oneCallResponse != null && it.airPollutionResponse != null }
        coVerify(exactly = 1) { locationUtil.getLocation() }
        assertEquals(address, state.lastUsedAddress)
        coVerify(exactly = 1) { getOneCallUseCase(address) }
        assertEquals(oneCallResponse, state.oneCallResponse)
        coVerify(exactly = 1) { getAirPollutionUseCase(address) }
        assertEquals(airPollutionResponse, state.airPollutionResponse)
        assertEquals(null, state.errorType)
    }

    @Test
    fun fetchWeather_withNullLocation_showsLocationError() = runTest(testDispatcher) {
        coEvery { locationUtil.getLocation() } returns null

        viewModel.fetchWeather()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.first { it.errorType != null }
        coVerify(exactly = 1) { locationUtil.getLocation() }
        assertEquals(ErrorType.LOCATION_ERROR, state.errorType)
    }

    @Test
    fun fetchWeather_withWeatherError_showsWeatherError() = runTest(testDispatcher) {
        val address = mockk<Address>()

        coEvery { locationUtil.getLocation() } returns address
        coEvery { getOneCallUseCase(address) } returns flowOf(Result.failure(Exception()))
        coEvery { getAirPollutionUseCase(address) } returns flowOf(Result.success(mockk()))

        viewModel.fetchWeather()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.first { it.errorType != null }
        coVerify(exactly = 1) { locationUtil.getLocation() }
        coVerify(exactly = 1) { getOneCallUseCase(address) }
        coVerify(exactly = 1) { getAirPollutionUseCase(address) }
        assertEquals(ErrorType.WEATHER_ERROR, state.errorType)
    }

    @Test
    fun fetchWeather_withAirPollutionError_showsWeatherError() = runTest(testDispatcher) {
        val address = mockk<Address>()
        val oneCallResponse = mockk<OneCallResponse>()

        coEvery { locationUtil.getLocation() } returns address
        coEvery { getOneCallUseCase(address) } returns flowOf(Result.success(oneCallResponse))
        coEvery { getAirPollutionUseCase(address) } returns flowOf(Result.failure(Exception()))

        viewModel.fetchWeather()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.first { it.errorType != null }
        coVerify(exactly = 1) { locationUtil.getLocation() }
        coVerify(exactly = 1) { getOneCallUseCase(address) }
        coVerify(exactly = 1) { getAirPollutionUseCase(address) }
        assertEquals(ErrorType.WEATHER_ERROR, state.errorType)
    }
}