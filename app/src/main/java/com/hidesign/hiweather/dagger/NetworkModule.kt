package com.hidesign.hiweather.dagger

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.hidesign.hiweather.network.WeatherApi
import com.hidesign.hiweather.util.Constants
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    fun getAPIKey(context: Context, key: String): String {
        val ai: ApplicationInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData.getString(key, "")
        return value.toString()
    }

    @Provides
    fun provideWeatherApiKey(context: Context): String {
        return getAPIKey(context, Constants.OPENWEATHER_KEY)
    }

    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .readTimeout(20L, TimeUnit.SECONDS)
                    .connectTimeout(20L, TimeUnit.SECONDS)
                    .addInterceptor(OkHttpProfilerInterceptor())
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        this.level = HttpLoggingInterceptor.Level.BODY
                    }).build()
            ).build()
    }

    @Provides
    fun provideApiService(retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }
}