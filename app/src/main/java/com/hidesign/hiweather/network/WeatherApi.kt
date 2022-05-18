package com.hidesign.hiweather.network

import com.hidesign.hiweather.models.LocationResult
import com.hidesign.hiweather.models.WeatherCurrent
import com.hidesign.hiweather.models.WeatherForecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApi {
    @GET("currentconditions/v1/{locationKey}?apikey=aDv8fsGqxTBQ0zmXKfqxLA53uuCnJK4Z&details=true")
    suspend fun getCurrentConditions(
        @Path("locationKey") locationKey: String,
        //@Query("apikey") apiKey: String,
    ): Response<WeatherCurrent?>?

    @GET("forecasts/v1/daily/5day/{locationKey}?apikey=aDv8fsGqxTBQ0zmXKfqxLA53uuCnJK4Z&language=en-us&details=true&metric=true")
    suspend fun getFiveDayForecast(
        @Path("locationKey") locationKey: String,
        //@Query("apikey") apiKey: String,
    ): Response<WeatherForecast?>?

    @GET("locations/v1/cities/search")
    suspend fun getLocation(
        @Query("apikey") apiKey: String,
        @Query("q") search: String,
    ): Response<LocationResult?>?
}