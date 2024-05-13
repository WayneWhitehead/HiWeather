package com.hidesign.hiweather.network

import android.content.Context
import android.location.Address
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hidesign.hiweather.model.AirPollutionResponse
import com.hidesign.hiweather.model.ErrorType
import com.hidesign.hiweather.model.OneCallResponse
import com.hidesign.hiweather.model.UIStatus
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.util.LocationUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationUtil: LocationUtil
) : ViewModel() {

    private val _lastUsedAddress = MutableLiveData<Address?>()
    val lastUsedAddress: LiveData<Address?> = _lastUsedAddress

    private val _oneCallResponse = MutableLiveData<OneCallResponse>()
    val oneCallResponse: LiveData<OneCallResponse> = _oneCallResponse

    private val _airPollutionResponse = MutableLiveData<AirPollutionResponse>()
    val airPollutionResponse: LiveData<AirPollutionResponse> = _airPollutionResponse

    private val _uiState = MutableLiveData<UIStatus>(UIStatus.Loading)
    val uiState: LiveData<UIStatus> = _uiState

    fun fetchWeather(address: Address? = null, context: Context) {
        viewModelScope.launch {
            _uiState.value = UIStatus.Loading
            try {
                val location = address ?: locationUtil.getLocation()
                if (location == null) {
                    _uiState.value = UIStatus.Error(ErrorType.LOCATION_ERROR)
                    return@launch
                }

                _lastUsedAddress.value = location
                getOneCallWeather(location, Constants.getUnit(context))
                getAirPollution(location)
            } catch (e: Exception) {
                _uiState.value = UIStatus.Error(ErrorType.LOCATION_ERROR)
            }
        }
    }

    private fun getOneCallWeather(address: Address, unit: String) {
        viewModelScope.launch {
            val response = weatherRepository.getWeather(address, unit)
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
            val response = weatherRepository.getAirPollution(address)
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
}