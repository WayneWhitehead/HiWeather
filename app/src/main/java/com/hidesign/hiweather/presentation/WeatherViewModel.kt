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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getOneCallUseCase: GetOneCallUseCase,
    private val getAirPollutionUseCase: GetAirPollutionUseCase,
    private val locationUtil: LocationUtil
) : ViewModel() {
    data class WeatherState(
        var lastUsedAddress: Address? = null,
        var oneCallResponse: OneCallResponse? = null,
        var airPollutionResponse: AirPollutionResponse? = null,
        val errorType: ErrorType? = null,
        val isLoading: Boolean = false
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

    private suspend fun getOneCall(address: Address) = withContext(Dispatchers.IO) {
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

    private suspend fun getAirPollution(address: Address) = withContext(Dispatchers.IO) {
        val response = getAirPollutionUseCase(address)
        response.onSuccess { airPollutionResponse ->
            airPollutionResponse?.let { airPollution ->
                _state.emit(state.value.copy(airPollutionResponse = airPollution))
            } ?: run {
                _state.emit(state.value.copy(errorType = ErrorType.WEATHER_ERROR))
            }
        }
        response.onFailure {
            showErrorDialog(ErrorType.WEATHER_ERROR)
        }
    }

    suspend fun showErrorDialog(errorType: ErrorType) = withContext(Dispatchers.Main) {
        _state.emit(state.value.copy(errorType = errorType))
    }

    private fun hideErrorDialog() = viewModelScope.launch {
        _state.emit(state.value.copy(errorType = null))
    }
}