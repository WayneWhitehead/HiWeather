package com.hidesign.hiweather.data.repository

import android.location.Address
import com.hidesign.hiweather.data.model.OneCallResponse
import com.hidesign.hiweather.domain.repository.WeatherApi
import com.hidesign.hiweather.domain.repository.WeatherRepository
import com.hidesign.hiweather.data.model.AirPollutionResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApi
): WeatherRepository {
    override suspend fun getOneCall(address: Address): Result<OneCallResponse?> {
        val response = apiService.getOneCall(address.latitude, address.longitude)
        return if (response.isSuccessful) {
            Result.success(response.body())
        } else {
            Result.failure(Exception(response.message()))
        }
    }

    override suspend fun getAirPollution(address: Address): Result<AirPollutionResponse?> {
        val response = apiService.getAirPollution(address.latitude, address.longitude)
        return if (response.isSuccessful) {
            Result.success(response.body())
        } else {
            Result.failure(Exception(response.message()))
        }
    }
}