package com.hidesign.hiweather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HiWeatherAppTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private var workerFactory: HiltWorkerFactory = mockk()
    private val hiWeatherApp = HiWeatherApp()

    @Test
    fun `getWorkManagerConfiguration should return a Configuration object that configures WorkManager to use the HiltWorkerFactory`() {
        // Call the getWorkManagerConfiguration() method
        every { hiWeatherApp.getWorkManagerConfiguration() } returns Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

        val result = hiWeatherApp.getWorkManagerConfiguration()

        // Assert that the result is a Configuration object that configures WorkManager to use the HiltWorkerFactory
        assertEquals(
            Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build(),
            result
        )
    }
}