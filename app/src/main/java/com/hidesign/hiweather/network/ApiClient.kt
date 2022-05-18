package com.hidesign.hiweather.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {

    var apiService: WeatherApi? = null
    private var retrofit: Retrofit? = null
    private val baseUrl = "http://dataservice.accuweather.com/"

    init {
        val httpClient = OkHttpClient.Builder().apply {
            readTimeout(30L, TimeUnit.SECONDS)
            connectTimeout(30L, TimeUnit.SECONDS)
            addInterceptor(HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BODY
            })
        }

        baseUrl.let {
            retrofit = Retrofit.Builder()
                .baseUrl(it)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                .client(httpClient.build())
                .build()
        }
        apiService = retrofit?.create(WeatherApi::class.java)
    }
}