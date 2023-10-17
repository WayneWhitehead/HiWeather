package com.hidesign.hiweather.network

import com.hidesign.hiweather.model.AirPollutionResponse
import com.hidesign.hiweather.model.OneCallResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val apiService: WeatherApi,
    private val weatherApiKey: String
) {
    suspend fun getWeather(lat: Double, lon: Double, unit: String): Response<OneCallResponse?> {
        val response = apiService.getOneCall(lat, lon, weatherApiKey, unit)
        if (response.isSuccessful) {
            return response
        } else {
            throw Exception(response.message())
        }
    }

    suspend fun getAirPollution(lat: Double, lon: Double): Response<AirPollutionResponse?> {
        val response = apiService.getAirPollution(lat, lon, weatherApiKey)
        if (response.isSuccessful) {
            return response
        } else {
            throw Exception(response.message())
        }
    }
}
