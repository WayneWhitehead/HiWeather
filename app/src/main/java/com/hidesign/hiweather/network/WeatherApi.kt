package com.hidesign.hiweather.network

import com.hidesign.hiweather.model.AirPollutionResponse
import com.hidesign.hiweather.model.HistoricalWeather
import com.hidesign.hiweather.model.OneCallResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("3.0/onecall")
    suspend fun getOneCall(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String = "minutely,hourly",
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
    ): Response<OneCallResponse?>?

    @GET("3.0/onecall")
    fun getOneCallWidget(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String = "minutely,hourly",
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
    ): Call<OneCallResponse?>?

    @GET("3.0/onecall/timemachine")
    suspend fun getOneCallHistory(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("dt") dt: Long,
        @Query("appid") apiKey: String,
    ): Response<HistoricalWeather?>?

    @GET("2.5/air_pollution")
    suspend fun getAirPollution(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
    ): Response<AirPollutionResponse?>?
}