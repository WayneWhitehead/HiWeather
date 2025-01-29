package com.hidesign.hiweather.dagger

import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class NetworkModuleTest {

    private lateinit var mockInterceptor: OkHttpProfilerInterceptor

    @Before
    fun setUp() {
        mockInterceptor = mock(OkHttpProfilerInterceptor::class.java)
    }

    @Test
    fun provideOkHttpClient_returnsNonNullClient() {
        val okHttpClient = NetworkModule.provideOkHttpClient(mockInterceptor)
        assertNotNull(okHttpClient)
    }

    @Test
    fun provideOkHttpClient_hasCorrectTimeouts() {
        val okHttpClient = NetworkModule.provideOkHttpClient(mockInterceptor)
        assertTrue(okHttpClient.readTimeoutMillis == 20000)
        assertTrue(okHttpClient.connectTimeoutMillis == 20000)
    }

    @Test
    fun provideRetrofit_returnsNonNullRetrofit() {
        val okHttpClient = NetworkModule.provideOkHttpClient(mockInterceptor)
        val retrofit = NetworkModule.provideRetrofit(okHttpClient)
        assertNotNull(retrofit)
    }

    @Test
    fun provideRetrofit_hasCorrectBaseUrl() {
        val okHttpClient = NetworkModule.provideOkHttpClient(mockInterceptor)
        val retrofit = NetworkModule.provideRetrofit(okHttpClient)
        assertTrue(retrofit.baseUrl().toString() == "https://api.openweathermap.org/data/")
    }

    @Test
    fun provideWeatherApi_returnsNonNullApi() {
        val okHttpClient = NetworkModule.provideOkHttpClient(mockInterceptor)
        val retrofit = NetworkModule.provideRetrofit(okHttpClient)
        val weatherApi = NetworkModule.provideWeatherApi(retrofit)
        assertNotNull(weatherApi)
    }

    @Test
    fun provideOkHttpProfilerInterceptor_returnsNonNullInterceptor() {
        val interceptor = NetworkModule.provideOkHttpProfilerInterceptor()
        assertNotNull(interceptor)
    }
}