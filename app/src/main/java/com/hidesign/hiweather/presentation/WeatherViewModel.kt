package com.hidesign.hiweather.presentation

import android.location.Address
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hidesign.hiweather.data.model.AirPollutionResponse
import com.hidesign.hiweather.data.model.Components
import com.hidesign.hiweather.data.model.Daily
import com.hidesign.hiweather.data.model.ErrorType
import com.hidesign.hiweather.data.model.FutureWeather
import com.hidesign.hiweather.data.model.OneCallResponse
import com.hidesign.hiweather.domain.usecase.GetAirPollutionUseCase
import com.hidesign.hiweather.domain.usecase.GetOneCallUseCase
import com.hidesign.hiweather.util.LocationUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getOneCallUseCase: GetOneCallUseCase,
    private val getAirPollutionUseCase: GetAirPollutionUseCase,
    private val locationUtil: LocationUtil
) : ViewModel() {
    data class AirPollutionDialogState(val show: Boolean = false, val components: Components = Components(), val title: String = "")
    data class SunMoonDialogState(val show: Boolean = false, val daily: Daily = Daily(), val timezone: String = "")
    data class ForecastDialogState(val show: Boolean = false , val weather: FutureWeather = Daily(), val timezone: String = "")
    data class WeatherState(
        var lastUsedAddress: Address? = null,
        var oneCallResponse: OneCallResponse? = null,
        var airPollutionResponse: AirPollutionResponse? = null,
        var airPollutionDialogState: AirPollutionDialogState = AirPollutionDialogState(),
        var celestialDialogState: SunMoonDialogState = SunMoonDialogState(),
        var forecastDialogState: ForecastDialogState = ForecastDialogState(),
        val errorType: ErrorType? = null,
        val isLoading: Boolean = false
    )

    private val _state = MutableStateFlow(WeatherState())
    val state: StateFlow<WeatherState> = _state.asStateFlow()

    fun fetchWeather(address: Address? = null) {
        viewModelScope.launch {
            try {
                val location = address ?: locationUtil.getLocation()
                if (location == null) {
                    showErrorDialog(ErrorType.LOCATION_ERROR)
                    this.cancel()
                } else {
                    _state.emit(state.value.copy(
                        lastUsedAddress = location,
                        isLoading = true,
                        oneCallResponse = null,
                        airPollutionResponse = null
                    ))
                    getOneCall(location)
                    getAirPollution(location)
                }
            } catch (e: Exception) {
                showErrorDialog(ErrorType.LOCATION_ERROR)
            }
        }
    }

    private fun getOneCall(address: Address) {
        viewModelScope.launch {
            val response = getOneCallUseCase(address)
            response.onSuccess { oneCallResponse ->
                oneCallResponse?.let { oneCall ->
                    _state.emit(state.value.copy(oneCallResponse = oneCall))
                } ?: run {
                    showErrorDialog(ErrorType.WEATHER_ERROR)
                }
            }
            response.onFailure {
                showErrorDialog(ErrorType.WEATHER_ERROR)
            }
        }
    }

    private fun getAirPollution(address: Address) {
        viewModelScope.launch {
            val response = getAirPollutionUseCase(address)
            response.onSuccess { airPollutionResponse ->
                airPollutionResponse?.let { airPollution ->
                    _state.emit(state.value.copy(airPollutionResponse = airPollution))
                } ?: run {
                    _state.emit(state.value.copy(errorType = ErrorType.WEATHER_ERROR))
                }
            }
            response.onFailure { _state.emit(state.value.copy(errorType = ErrorType.WEATHER_ERROR)) }
        }
    }

    fun showAirPollutionDialog(components: Components, title: String) { viewModelScope.launch(Dispatchers.Main) {
        _state.emit(state.value.copy(airPollutionDialogState = _state.value.airPollutionDialogState.copy(show = true, components = components, title = title)))
    }}

    fun hideAirPollutionDialog() { viewModelScope.launch(Dispatchers.Main) {
        _state.emit(state.value.copy(airPollutionDialogState = AirPollutionDialogState()))
    }}

    fun showCelestialDialog(daily: Daily, timezone: String) { viewModelScope.launch(Dispatchers.Main) {
        _state.emit(state.value.copy(celestialDialogState = _state.value.celestialDialogState.copy(show = true, daily = daily, timezone = timezone)))
    }}

    fun hideCelestialDialog() { viewModelScope.launch(Dispatchers.Main) {
        _state.emit(state.value.copy(celestialDialogState = SunMoonDialogState()))
    }}

    fun showForecastDialog(weather: FutureWeather, timezone: String) { viewModelScope.launch(Dispatchers.Main) {
        _state.emit(state.value.copy(forecastDialogState = _state.value.forecastDialogState.copy(show = true, weather = weather, timezone = timezone)))
    }}

    fun hideForecastDialog() { viewModelScope.launch(Dispatchers.Main) {
        _state.emit(state.value.copy(forecastDialogState = ForecastDialogState()))
    }}

    fun showErrorDialog(errorType: ErrorType) { viewModelScope.launch(Dispatchers.Main) {
        _state.emit(state.value.copy(errorType = errorType))
    }}

    fun hideErrorDialog() { viewModelScope.launch(Dispatchers.Main) {
        _state.emit(state.value.copy(errorType = null))
    }}
}