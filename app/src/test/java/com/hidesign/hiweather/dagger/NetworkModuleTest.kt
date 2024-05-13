package com.hidesign.hiweather.dagger

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import com.hidesign.hiweather.network.WeatherApi
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.mockk
import io.mockk.unmockkAll
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(MockitoJUnitRunner::class)
@Config(manifest=Config.NONE)
class NetworkModuleTest {

    @InjectMockKs
    val context: Context = mockk(relaxed = true)

    private val retrofit: Retrofit = mockk(relaxed = true)
    private val weatherApi: WeatherApi = mockk(relaxed = true)

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        every { retrofit.create(WeatherApi::class.java) } returns weatherApi
    }

    @Test
    fun `should provide the correct weather API key`() {
        val key = "weatherKey"
        val expectedValue = "YOUR_API_KEY"

        val bundle = mockk<Bundle>()
        every { bundle.getString(key, "") } returns expectedValue

        val applicationInfo = mockk<ApplicationInfo>()
        applicationInfo.metaData = bundle

        every { context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA) } returns applicationInfo

        val actualValue = NetworkModule.provideWeatherApiKey(context)
        Assert.assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `provideRetrofit should return correct Retrofit object`() {
        val result = NetworkModule.provideRetrofit()
        val client = result.callFactory() as OkHttpClient

        assert(result.baseUrl().toString() == "https://api.openweathermap.org/data/")
        assert(result.converterFactories()[1] is GsonConverterFactory)
        assert(result.callFactory() is OkHttpClient)
        assert(client.interceptors[0] is OkHttpProfilerInterceptor)
        assert(client.interceptors[1] is HttpLoggingInterceptor)
    }

    @Test
    fun `provideApiService should return WeatherApi object`() {
        val result = NetworkModule.provideApiService(retrofit)
        assert(result is WeatherApi)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}
