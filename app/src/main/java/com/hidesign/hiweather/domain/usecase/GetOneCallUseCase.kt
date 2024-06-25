package com.hidesign.hiweather.domain.usecase

import android.location.Address
import com.hidesign.hiweather.data.model.OneCallResponse
import com.hidesign.hiweather.domain.repository.WeatherRepository
import javax.inject.Inject

class GetOneCallUseCase @Inject constructor(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(location: Address): Result<OneCallResponse?> {
        return weatherRepository.getOneCall(location)
    }
}