package com.hidesign.hiweather.domain.repository

import com.hidesign.hiweather.data.model.OneCallResponse
import com.hidesign.hiweather.data.model.AirPollutionResponse
import com.hidesign.hiweather.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("3.0/onecall")
    suspend fun getOneCall(
        @Query("lat") lat: Double = 0.0,
        @Query("lon") lon: Double = 0.0,
        @Query("exclude") exclude: String = "minutely",
        @Query("units") units: String = Constants.getUnit(),
    ): Response<OneCallResponse?>

    @GET("2.5/air_pollution")
    suspend fun getAirPollution(
        @Query("lat") lat: Double = 0.0,
        @Query("lon") lon: Double = 0.0,
    ): Response<AirPollutionResponse?>
}