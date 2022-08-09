package com.hidesign.hiweather.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hidesign.hiweather.model.AirPollutionResponse
import com.hidesign.hiweather.model.OneCallResponse
import com.hidesign.hiweather.views.WeatherActivity.Companion.uAddress


class WeatherViewModel : ViewModel() {

    private val _oneCallResponse = MutableLiveData<OneCallResponse>()
    val oneCallResponse: LiveData<OneCallResponse> get() = _oneCallResponse

    private val _airPollutionResponse = MutableLiveData<AirPollutionResponse>()
    val airPollutionResponse: LiveData<AirPollutionResponse> get() = _airPollutionResponse

    suspend fun getOneCallWeather(key: String, unit: String) {
        val apiClient = getApiClient()
        val tempUnit = when (unit) {
            "celsius" -> "metric"
            "fahrenheit" -> "imperial"
            "kelvin" -> ""
            else -> "metric"
        }

        val response = apiClient?.getOneCall(
            uAddress!!.latitude,
            uAddress!!.longitude,
            "minutely",
            key,
            tempUnit)

        if (response!!.isSuccessful) {
            _oneCallResponse.value = response.body()
        }
    }

    suspend fun getAirPollution(key: String) {
        val apiClient = getApiClient()
        val response = apiClient?.getAirPollution(uAddress!!.latitude, uAddress!!.longitude, key)

        if (response!!.isSuccessful) {
            _airPollutionResponse.value = response.body()
        }
    }

    private fun getApiClient() = ApiClient().apiService
}

