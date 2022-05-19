package com.hidesign.hiweather.network

import OneCallResponse
import com.hidesign.hiweather.model.AirPollutionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("onecall")
    suspend fun getOneCall(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String = "minutely,hourly",
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<OneCallResponse?>?

    @GET("air_pollution")
    suspend fun getAirPollution(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
    ): Response<AirPollutionResponse?>?
}