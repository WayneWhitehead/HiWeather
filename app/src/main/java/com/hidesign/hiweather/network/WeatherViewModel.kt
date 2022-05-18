package com.hidesign.hiweather.network

import androidx.lifecycle.ViewModel
import com.hidesign.hiweather.models.LocationResult
import com.hidesign.hiweather.models.WeatherCurrent
import com.hidesign.hiweather.models.WeatherForecast
import retrofit2.Response

class WeatherViewModel : ViewModel() {
    private val locationKey = "298833"
    private val apiKey = "aDv8fsGqxTBQ0zmXKfqxLA53uuCnJK4Z"
    private val alternateApiKey = "khcVaCtxEWMqANYYS6by6Rf5ZWiSda7p"

    suspend fun getLocation(search: String): Response<LocationResult?>? {
        val apiClient = getApiClient()
        return apiClient?.getLocation(apiKey, search)
    }

    suspend fun getCurrentConditions(): Response<WeatherCurrent?>? {
        val apiClient = getApiClient()
        return apiClient?.getCurrentConditions(locationKey)
    }

    suspend fun getCurrentConditions(location: String): Response<WeatherCurrent?>? {
        val apiClient = getApiClient()
        return apiClient?.getCurrentConditions(location)
    }

    suspend fun getFiveDayForecast(): Response<WeatherForecast?>? {
        val apiClient = getApiClient()
        return apiClient?.getFiveDayForecast(locationKey)
    }

    suspend fun getFiveDayForecast(location: String): Response<WeatherForecast?>? {
        val apiClient = getApiClient()
        return apiClient?.getFiveDayForecast(location)
    }

    private fun getApiClient() = ApiClient().apiService
}

