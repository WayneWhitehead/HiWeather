package com.hidesign.hiweather.network

import com.hidesign.hiweather.model.OneCallResponse
import androidx.lifecycle.ViewModel
import com.hidesign.hiweather.model.AirPollutionResponse
import com.hidesign.hiweather.model.HistoricalWeather
import retrofit2.Response

class WeatherViewModel : ViewModel() {

    suspend fun getOneCallWeather(lat: Double, lon: Double, key: String): Response<OneCallResponse?>? {
        val apiClient = getApiClient()
        return apiClient?.getOneCall(lat, lon, "minutely", key, "metric")
    }

    suspend fun getHistoricalWeather(lat: Double, lon: Double, dt: Long, key: String): Response<HistoricalWeather?>? {
        val apiClient = getApiClient()
        return apiClient?.getOneCallHistory(lat, lon, dt, key)
    }

    suspend fun getAirPollution(lat: Double, lon: Double, key: String): Response<AirPollutionResponse?>? {
        val apiClient = getApiClient()
        return apiClient?.getAirPollution(lat, lon, key)
    }

    private fun getApiClient() = ApiClient().apiService
}

