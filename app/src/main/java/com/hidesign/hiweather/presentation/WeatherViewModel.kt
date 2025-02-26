package com.hidesign.hiweather.presentation

import android.location.Address
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hidesign.hiweather.data.model.AirPollutionResponse
import com.hidesign.hiweather.data.model.ErrorType
import com.hidesign.hiweather.data.model.OneCallResponse
import com.hidesign.hiweather.domain.usecase.GetAirPollutionUseCase
import com.hidesign.hiweather.domain.usecase.GetOneCallUseCase
import com.hidesign.hiweather.util.LocationUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class WeatherViewModel @Inject constructor(
    @Named("io") private val io: CoroutineContext,
    @Named("main") private val main: CoroutineContext,
    private val getOneCallUseCase: GetOneCallUseCase,
    private val getAirPollutionUseCase: GetAirPollutionUseCase,
    private val locationUtil: LocationUtil
) : ViewModel() {
    data class WeatherState(
        var lastUsedAddress: Address? = null,
        var oneCallResponse: OneCallResponse? = null,
        var airPollutionResponse: AirPollutionResponse? = null,
        val errorType: ErrorType? = null
    )

    private val _state = MutableStateFlow(WeatherState())
    val state: StateFlow<WeatherState> = _state.asStateFlow()

    fun fetchWeather(address: Address? = null) {
        viewModelScope.launch {
            hideErrorDialog()
            try {
                val location = address ?: locationUtil.getLocation()
                if (location == null) {
                    showErrorDialog(ErrorType.LOCATION_ERROR)
                } else {
                    _state.emit(state.value.copy(
                        lastUsedAddress = location,
                        oneCallResponse = null,
                        airPollutionResponse = null
                    ))
                    fetchWeatherData(location)
                }
            } catch (e: Exception) {
                showErrorDialog(ErrorType.LOCATION_ERROR)
            }
        }
    }

    private suspend fun fetchWeatherData(address: Address) = coroutineScope {
        getOneCallUseCase(address).collect { result ->
            result.fold(
                onSuccess = { oneCallResponse ->
                    oneCallResponse?.let { oneCall ->
                        _state.emit(state.value.copy(oneCallResponse = oneCall))
                    } ?: run {
                        showErrorDialog(ErrorType.WEATHER_ERROR)
                    }
                },
                onFailure = {
                    showErrorDialog(ErrorType.WEATHER_ERROR)
                }
            )
        }
        getAirPollutionUseCase(address).collect { result ->
            result.fold(
                onSuccess = { airPollutionResponse ->
                    airPollutionResponse?.let { airPollution ->
                        _state.emit(state.value.copy(airPollutionResponse = airPollution))
                    } ?: run {
                        showErrorDialog(ErrorType.WEATHER_ERROR)
                    }
                },
                onFailure = {
                    showErrorDialog(ErrorType.WEATHER_ERROR)
                }
            )
        }
    }

    suspend fun showErrorDialog(errorType: ErrorType) = withContext(main) {
        _state.emit(state.value.copy(errorType = errorType))
    }

    private suspend fun hideErrorDialog() = withContext(main) {
        _state.emit(state.value.copy(errorType = null))
    }
}