package com.hidesign.hiweather.domain.usecase

import android.location.Address
import com.hidesign.hiweather.domain.repository.WeatherRepository
import com.hidesign.hiweather.data.model.AirPollutionResponse
import javax.inject.Inject

class GetAirPollutionUseCase @Inject constructor(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(address: Address): Result<AirPollutionResponse?> {
        return weatherRepository.getAirPollution(address)
    }
}