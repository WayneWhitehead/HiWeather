package com.hidesign.hiweather.dagger

import com.hidesign.hiweather.data.repository.WeatherRepositoryImpl
import com.hidesign.hiweather.domain.repository.WeatherApi
import com.hidesign.hiweather.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun provideRepository(apiService: WeatherApi): WeatherRepository {
        return WeatherRepositoryImpl(apiService)
    }
}