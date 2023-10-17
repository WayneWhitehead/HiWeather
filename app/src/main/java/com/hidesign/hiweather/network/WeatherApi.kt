package com.hidesign.hiweather.network

import com.hidesign.hiweather.model.AirPollutionResponse
import com.hidesign.hiweather.model.OneCallResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("3.0/onecall")
    suspend fun getOneCall(
        @Query("lat") lat: Double = 0.0,
        @Query("lon") lon: Double = 0.0,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("exclude") exclude: String = "minutely",
    ): Response<OneCallResponse?>

    @GET("2.5/air_pollution")
    suspend fun getAirPollution(
        @Query("lat") lat: Double = 0.0,
        @Query("lon") lon: Double = 0.0,
        @Query("appid") apiKey: String,
    ): Response<AirPollutionResponse?>
}