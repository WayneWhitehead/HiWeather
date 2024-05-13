package com.hidesign.hiweather.network

import android.location.Address
import com.hidesign.hiweather.model.AirPollutionResponse
import com.hidesign.hiweather.model.OneCallResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val apiService: WeatherApi,
    private val weatherApiKey: String
) {
    suspend fun getWeather(address: Address, unit: String): Result<OneCallResponse?> {
        val response = apiService.getOneCall(address.latitude, address.longitude, weatherApiKey, unit)
        return if (response.isSuccessful) {
            Result.success(response.body())
        } else {
            Result.failure(Exception(response.message()))
        }
    }

    suspend fun getAirPollution(address: Address): Result<AirPollutionResponse?> {
        val response = apiService.getAirPollution(address.latitude, address.longitude, weatherApiKey)
        return if (response.isSuccessful) {
            Result.success(response.body())
        } else {
            Result.failure(Exception(response.message()))
        }
    }
}