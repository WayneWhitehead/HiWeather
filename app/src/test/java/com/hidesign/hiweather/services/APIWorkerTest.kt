package com.hidesign.hiweather.services

import android.content.Context
import android.location.Address
import androidx.work.WorkerParameters
import com.hidesign.hiweather.data.model.AirPollutionResponse
import com.hidesign.hiweather.data.model.OneCallResponse
import com.hidesign.hiweather.domain.usecase.GetAirPollutionUseCase
import com.hidesign.hiweather.domain.usecase.GetOneCallUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.Result.Companion as KotlinResult


class APIWorkerTest {

    private val context = mockk<Context>(relaxed = true)
    private val workerParams = mockk<WorkerParameters>(relaxed = true)
    private val getOneCallUseCase = mockk<GetOneCallUseCase>()
    private val getAirPollutionUseCase = mockk<GetAirPollutionUseCase>()
    private val apiWorker = APIWorker(context, workerParams, getOneCallUseCase, getAirPollutionUseCase)
    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun doWork_weatherUpdatesEnabled_success() = runTest(testDispatcher) {
        val oneCallResponse = mockk<OneCallResponse>()
        val airPollutionResponse = mockk<AirPollutionResponse>()
        val mockAddress = mockk<Address>(relaxed = true).apply {
            coEvery { latitude } returns 37.7749
            coEvery { longitude } returns -122.4194
        }

        coEvery { getOneCallUseCase(mockAddress) } returns flowOf(Result.success(oneCallResponse))
        coEvery { getAirPollutionUseCase(mockAddress) } returns flowOf(KotlinResult.success(airPollutionResponse))

        val result = apiWorker.doWork()

        assertEquals(flowOf(Result.success(airPollutionResponse)), result)
    }

    @Test
    fun doWork_weatherUpdatesEnabled_failure() = runTest(testDispatcher) {
        val mockAddress = mockk<Address>(relaxed = true).apply {
            coEvery { latitude } returns 37.7749
            coEvery { longitude } returns -122.4194
        }

        coEvery { getOneCallUseCase(mockAddress) } returns flowOf(Result.failure(Exception()))

        val result = apiWorker.doWork()

        assertEquals(flowOf(Result.failure<Throwable>(Exception())), result)
    }

    @Test
    fun doWork_airUpdatesEnabled_success() = runTest(testDispatcher) {
        val oneCallResponse = mockk<OneCallResponse>()
        val airPollutionResponse = mockk<AirPollutionResponse>()
        val mockAddress = mockk<Address>(relaxed = true).apply {
            coEvery { latitude } returns 37.7749
            coEvery { longitude } returns -122.4194
        }

        coEvery { getOneCallUseCase(mockAddress) } returns flowOf(Result.success(oneCallResponse))
        coEvery { getAirPollutionUseCase(mockAddress) } returns flowOf(Result.success(airPollutionResponse))

        val result = apiWorker.doWork()

        assertEquals(flowOf(Result.success(airPollutionResponse)), result)
    }

    @Test
    fun doWork_airUpdatesEnabled_failure() = runTest(testDispatcher) {
        val mockAddress = mockk<Address>(relaxed = true).apply {
            coEvery { latitude } returns 37.7749
            coEvery { longitude } returns -122.4194
        }

        coEvery { getAirPollutionUseCase(mockAddress) } returns flowOf(Result.failure(Exception()))

        val result = apiWorker.doWork()

        assertEquals(flowOf(Result.failure<Throwable>(Exception())), result)
    }

    @Test
    fun doWork_noUpdatesEnabled() = runTest(testDispatcher) {
        val result = apiWorker.doWork()

        assertEquals(flowOf(Result.success(true)), result)
    }
}