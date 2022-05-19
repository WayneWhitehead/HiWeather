package com.hidesign.hiweather.network

import OneCallResponse
import androidx.lifecycle.ViewModel
import com.hidesign.hiweather.model.AirPollutionResponse
import retrofit2.Response

class WeatherViewModel : ViewModel() {
    private val apikey = "8f1aae21fcdc65d7bee6147335281369"

    suspend fun getOneCallWeather(lat: Double, lon: Double): Response<OneCallResponse?>? {
        val apiClient = getApiClient()
        return apiClient?.getOneCall(lat, lon, "minutely", apikey, "metric")
    }

    suspend fun getAirPollution(lat: Double, lon: Double): Response<AirPollutionResponse?>? {
        val apiClient = getApiClient()
        return apiClient?.getAirPollution(lat, lon, apikey)
    }

    private fun getApiClient() = ApiClient().apiService
}

