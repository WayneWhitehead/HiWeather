package com.hidesign.hiweather

import android.os.Build
import android.os.Looper
import androidx.hilt.work.HiltWorkerFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import timber.log.Timber

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class HiWeatherAppTest {

    private lateinit var hiWeatherApp: HiWeatherApp
    private var workerFactory: HiltWorkerFactory = mockk()

    @Before
    fun setUp() {
        hiWeatherApp = HiWeatherApp()
        hiWeatherApp.workerFactory = workerFactory
        mockkStatic(BuildConfig::class)
        mockkStatic(Looper::class)
        every { Looper.getMainLooper() } returns mockk()
    }

    @Test
    fun workManagerConfiguration_returnsNonNullConfiguration() {
        val configuration = hiWeatherApp.workManagerConfiguration
        assertNotNull(configuration)
    }

    @Test
    fun workManagerConfiguration_hasCorrectWorkerFactory() {
        val configuration = hiWeatherApp.workManagerConfiguration
        assertTrue(configuration.workerFactory == workerFactory)
    }

    @Test
    fun onCreate_debugMode_plantsTimberDebugTree() {
        every { BuildConfig.DEBUG } returns true
        hiWeatherApp.onCreate()
        assertTrue(Timber.treeCount > 0)
    }

    @Test
    fun onCreate_nonDebugMode_doesNotPlantTimberDebugTree() {
        every { BuildConfig.DEBUG } returns false
        hiWeatherApp.onCreate()
        assertTrue(Timber.treeCount == 0)
    }
}