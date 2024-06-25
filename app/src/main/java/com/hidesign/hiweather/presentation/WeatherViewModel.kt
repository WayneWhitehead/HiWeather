package com.hidesign.hiweather.presentation

import android.location.Address
import androidx.lifecycle.*
import com.hidesign.hiweather.data.model.*
import com.hidesign.hiweather.domain.usecase.GetAirPollutionUseCase
import com.hidesign.hiweather.domain.usecase.GetOneCallUseCase
import com.hidesign.hiweather.util.LocationUtil
import dagger.hilt.android.lifecycle.HiltViewModel
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
    data class AirPollutionDialogState(val components: Components, val title: String)
    data class SunMoonDialogState(val daily: Daily, val timezone: String)
    data class ForecastDialogState(val weather: FutureWeather, val timezone: String)

    private val _lastUsedAddress = MutableLiveData<Address?>()
    val lastUsedAddress: LiveData<Address?> = _lastUsedAddress

    private val _oneCallResponse = MutableLiveData<OneCallResponse>()
    val oneCallResponse: LiveData<OneCallResponse> = _oneCallResponse

    private val _airPollutionResponse = MutableLiveData<AirPollutionResponse>()
    val airPollutionResponse: LiveData<AirPollutionResponse> = _airPollutionResponse

    private val _uiState = MutableLiveData<UIStatus>(UIStatus.Loading)
    val uiState: LiveData<UIStatus> = _uiState

    private val _airPollutionDialogState = MutableStateFlow<AirPollutionDialogState?>(null)
    val airPollutionDialogState: StateFlow<AirPollutionDialogState?> = _airPollutionDialogState.asStateFlow()

    private val _celestialDialogState = MutableStateFlow<SunMoonDialogState?>(null)
    val celestialDialogState: StateFlow<SunMoonDialogState?> = _celestialDialogState.asStateFlow()

    private val _forecastDialogState = MutableStateFlow<ForecastDialogState?>(null)
    val forecastDialogState: StateFlow<ForecastDialogState?> = _forecastDialogState.asStateFlow()

    fun fetchWeather(address: Address? = null) {
        viewModelScope.launch {
            _uiState.value = UIStatus.Loading
            try {
                val location = address ?: locationUtil.getLocation()
                if (location == null) {
                    _uiState.value = UIStatus.Error(ErrorType.LOCATION_ERROR)
                    return@launch
                }

                _lastUsedAddress.value = location
                getOneCall(location)
                getAirPollution(location)
            } catch (e: Exception) {
                _uiState.value = UIStatus.Error(ErrorType.LOCATION_ERROR)
            }
        }
    }

    private fun getOneCall(address: Address) {
        viewModelScope.launch {
            val response = getOneCallUseCase(address)
            response.onSuccess {
                _oneCallResponse.value = it
                _uiState.value = UIStatus.Success
            }
            response.onFailure {
                _uiState.value = UIStatus.Error(ErrorType.WEATHER_ERROR)
            }
        }
    }

    private fun getAirPollution(address: Address) {
        viewModelScope.launch {
            val response = getAirPollutionUseCase(address)
            response.onSuccess {
                _airPollutionResponse.value = it
                _uiState.value = UIStatus.Success
            }
            response.onFailure {
                _uiState.value = UIStatus.Error(ErrorType.WEATHER_ERROR)
            }
        }
    }

    fun updateUIState(status: UIStatus) {
        _uiState.value = status
    }

    fun showAirPollutionDialog(components: Components, title: String) {
        _airPollutionDialogState.value = AirPollutionDialogState(components, title)
    }

    fun hideAirPollutionDialog() {
        _airPollutionDialogState.value = null
    }

    fun showCelestialDialog(daily: Daily, timezone: String) {
        _celestialDialogState.value = SunMoonDialogState(daily, timezone)
    }

    fun hideCelestialDialog() {
        _celestialDialogState.value = null
    }

    fun showForecastDialog(weather: FutureWeather, timezone: String) {
        _forecastDialogState.value = ForecastDialogState(weather, timezone)
    }

    fun hideForecastDialog() {
        _forecastDialogState.value = null
    }
}