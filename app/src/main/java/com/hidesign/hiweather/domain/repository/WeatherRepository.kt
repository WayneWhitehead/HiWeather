package com.hidesign.hiweather.domain.repository

import android.location.Address
import com.hidesign.hiweather.data.model.OneCallResponse
import com.hidesign.hiweather.data.model.AirPollutionResponse

interface WeatherRepository {
    suspend fun getOneCall(address: Address): Result<OneCallResponse?>
    suspend fun getAirPollution(address: Address): Result<AirPollutionResponse?>
}