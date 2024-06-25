package com.hidesign.hiweather.dagger

import com.hidesign.hiweather.domain.repository.WeatherRepository
import com.hidesign.hiweather.domain.usecase.GetAirPollutionUseCase
import com.hidesign.hiweather.domain.usecase.GetOneCallUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Provides
    fun provideGetOneCallUseCase(weatherRepository: WeatherRepository): GetOneCallUseCase {
        return GetOneCallUseCase(weatherRepository)
    }

    @Provides
    fun provideGetAirPollutionUseCase(weatherRepository: WeatherRepository): GetAirPollutionUseCase {
        return GetAirPollutionUseCase(weatherRepository)
    }
}