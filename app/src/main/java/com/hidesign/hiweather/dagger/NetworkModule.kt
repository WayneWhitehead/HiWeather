package com.hidesign.hiweather.dagger

import com.hidesign.hiweather.BuildConfig
import com.hidesign.hiweather.domain.repository.WeatherApi
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideOkHttpClient(interceptor: OkHttpProfilerInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(20L, TimeUnit.SECONDS)
            .connectTimeout(20L, TimeUnit.SECONDS)
            .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val modifiedRequest = originalRequest.newBuilder()
                    .header("x-api-key", BuildConfig.OPENWEATHER_KEY)
                    .build()
                chain.proceed(modifiedRequest)
            }
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }

    @Provides
    fun provideOkHttpProfilerInterceptor(): OkHttpProfilerInterceptor {
        return OkHttpProfilerInterceptor()
    }
}