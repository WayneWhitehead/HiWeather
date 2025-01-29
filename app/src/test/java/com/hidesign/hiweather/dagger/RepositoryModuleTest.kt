package com.hidesign.hiweather.dagger

import com.hidesign.hiweather.data.repository.WeatherRepositoryImpl
import com.hidesign.hiweather.domain.repository.WeatherApi
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RepositoryModuleTest {

    @Test
    fun provideRepository_returnsNonNullRepository() {
        val apiService = mock(WeatherApi::class.java)
        val repository = RepositoryModule().provideRepository(apiService)
        assertNotNull(repository)
    }

    @Test
    fun provideRepository_returnsInstanceOfWeatherRepositoryImpl() {
        val apiService = mock(WeatherApi::class.java)
        val repository = RepositoryModule().provideRepository(apiService)
        assertTrue(repository is WeatherRepositoryImpl)
    }
}