package com.hidesign.hiweather.network

import android.content.Context
import android.location.Address
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hidesign.hiweather.model.AirPollutionResponse
import com.hidesign.hiweather.model.OneCallResponse
import com.hidesign.hiweather.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherRepository: WeatherRepository) : ViewModel() {

    private val _uiState = MutableLiveData<NetworkStatus>()
    val uiState: MutableLiveData<NetworkStatus> get() = _uiState

    private val _oneCallResponse = MutableLiveData<OneCallResponse>()
    val oneCallResponse: LiveData<OneCallResponse> get() = _oneCallResponse

    private val _airPollutionResponse = MutableLiveData<AirPollutionResponse>()
    val airPollutionResponse: LiveData<AirPollutionResponse> get() = _airPollutionResponse

    suspend fun getOneCallWeather(context: Context, uAddress: Address?) {
        _uiState.value = NetworkStatus.LOADING
        if (uAddress == null || !uAddress.hasLatitude() || !uAddress.hasLongitude()) return
        val response = weatherRepository.getWeather(
            uAddress.latitude,
            uAddress.longitude,
            Constants.getUnit(context))

        if (response.body() != null) {
            _oneCallResponse.value = response.body()
        } else {
            _uiState.value = NetworkStatus.ERROR
        }
    }

    suspend fun getAirPollution(uAddress: Address?) {
        _uiState.value = NetworkStatus.LOADING
        if (uAddress == null || !uAddress.hasLatitude() || !uAddress.hasLongitude()) return
        val response = weatherRepository.getAirPollution(uAddress.latitude, uAddress.longitude)

        if (response.body() != null) {
            _airPollutionResponse.value = response.body()
        } else {
            _uiState.value = NetworkStatus.ERROR
        }
    }

    fun updateUIState(status: NetworkStatus) {
        _uiState.value = status
    }
}

enum class NetworkStatus {
    LOADING,
    SUCCESS,
    ERROR
}



