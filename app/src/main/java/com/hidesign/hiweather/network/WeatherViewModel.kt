package com.hidesign.hiweather.network

import androidx.lifecycle.ViewModel
import com.hidesign.hiweather.model.AirPollutionResponse
import com.hidesign.hiweather.model.HistoricalWeather
import com.hidesign.hiweather.model.OneCallResponse
import com.hidesign.hiweather.views.WeatherActivity.Companion.uAddress
import retrofit2.Response

class WeatherViewModel : ViewModel() {

    suspend fun getOneCallWeather(key: String): Response<OneCallResponse?>? {
        val apiClient = getApiClient()
        return apiClient?.getOneCall(uAddress!!.latitude,
            uAddress!!.longitude,
            "minutely",
            key,
            "metric")
    }

    suspend fun getHistoricalWeather(dt: Long, key: String): Response<HistoricalWeather?>? {
        val apiClient = getApiClient()
        return apiClient?.getOneCallHistory(uAddress!!.latitude, uAddress!!.longitude, dt, key)
    }

    suspend fun getAirPollution(key: String): Response<AirPollutionResponse?>? {
        val apiClient = getApiClient()
        return apiClient?.getAirPollution(uAddress!!.latitude, uAddress!!.longitude, key)
    }

    private fun getApiClient() = ApiClient().apiService
}

