package com.hidesign.hiweather.domain.usecase

import android.location.Address
import com.hidesign.hiweather.data.model.AirPollutionResponse
import com.hidesign.hiweather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAirPollutionUseCase @Inject constructor(private val weatherRepository: WeatherRepository) {
    operator fun invoke(location: Address): Flow<Result<AirPollutionResponse?>> = flow {
        try {
            val result = weatherRepository.getAirPollution(location)
            emit(result)
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}