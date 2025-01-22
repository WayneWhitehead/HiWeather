package com.hidesign.hiweather.services
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.impl.utils.taskexecutor.SerialExecutor
import androidx.work.impl.utils.taskexecutor.TaskExecutor
import com.google.gson.Gson
import com.hidesign.hiweather.R
import com.hidesign.hiweather.data.model.OneCallResponse
import com.hidesign.hiweather.data.repository.WeatherRepositoryImpl
import com.hidesign.hiweather.services.APIWorker.Companion.WORK_NAME
import com.hidesign.hiweather.services.APIWorker.Companion.getWorkRequest
import com.hidesign.hiweather.services.APIWorker.Companion.updateWidget
import com.hidesign.hiweather.util.Constants
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyDouble
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import retrofit2.Response

class APIWorkerTest {

    private val weatherResponse = mockk<Response<OneCallResponse?>>()
    private val workManager: WorkManager = mockk()
    private val context: Context = mockk()
    private val weatherRepositoryImpl: WeatherRepositoryImpl = mockk()
    private lateinit var worker: APIWorker

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        WorkManager.initialize(context, mockk())

        every { context.applicationContext } returns context

        val workerParameters = mockk<WorkerParameters>()
        val taskExecutor = mockk<TaskExecutor>()

        every { workerParameters.taskExecutor } returns taskExecutor
        every { taskExecutor.serialTaskExecutor } returns mockk<SerialExecutor>()
        worker = APIWorker(context, workerParameters, weatherRepositoryImpl)

        val sharedPreferences = mockk<SharedPreferences>()
        every { context.getSharedPreferences(Constants.PREFERENCES, 0) } returns sharedPreferences
        every { sharedPreferences.getString(Constants.LATITUDE, "0.0") } returns "0.0"
        every { sharedPreferences.getString(Constants.LONGITUDE, "0.0") } returns "0.0"
        every { sharedPreferences.getString(Constants.LOCALITY, "") } returns ""
        every { sharedPreferences.getInt(Constants.TEMPERATURE_UNIT, 0) } returns 0
        every { context.resources.getStringArray(R.array.temperature_units) } returns arrayOf("Celsius", "Fahrenheit", "Kelvin")

        val notificationManager = mockk<NotificationManager>()
        every { context.getSystemService(Context.NOTIFICATION_SERVICE) } returns notificationManager

        val weatherResponse = mockk<Response<OneCallResponse?>>()
        every { runBlocking {
            weatherRepositoryImpl.getOneCall(anyDouble(), anyDouble(), anyString())
        } } returns weatherResponse

        // Mock the updateWidget() method
        //every { APIWorker.updateWidget(context) } returns Unit
    }

    @Test
    fun `doWork() should call the weather repository`() {
        runBlocking {
            worker.doWork()
        }

        verify {
            runBlocking {
                weatherRepositoryImpl.getOneCall(anyDouble(), anyDouble(), anyString())
            }
        }
    }

    @Test
    fun `doWork() should save the weather response to shared preferences`() {
        every {
            runBlocking {
                weatherRepositoryImpl.getOneCall(anyDouble(), anyDouble(), anyString())
            }
        } returns weatherResponse

        runBlocking {
            worker.doWork()
        }

        verify { context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).edit().putString(Constants.WEATHER_RESPONSE, Gson().toJson(weatherResponse)) }
    }

    @Test
    fun `doWork() should update the widget`() {
        runBlocking {
            worker.doWork()
        }

        verify {
            runBlocking {
                updateWidget(context)
            }
        }
    }

    @Test
    fun `doWork() should send a notification if notifications are enabled`() {
        val notificationManager = mockk<NotificationManager>()
        every { context.getSystemService(Context.NOTIFICATION_SERVICE) } returns notificationManager
        every { notificationManager.areNotificationsEnabled() } returns true

        runBlocking {
            worker.doWork()
        }

        verify { notificationManager.notify(eq(1), any()) }
    }

    @Test
    fun `doWork() should not send a notification if notifications are not enabled`() {
        val notificationManager = mockk<NotificationManager>()
        every { context.applicationContext } returns context
        every { context.getSystemService(Context.NOTIFICATION_SERVICE) } returns notificationManager
        every { notificationManager.areNotificationsEnabled() } returns false

        runBlocking {
            worker.doWork()
        }

        verify(never()) { notificationManager.notify(eq(1), any()) }
    }

    @Test
    fun testInitWorker() {
        every { context.applicationContext } returns context
        APIWorker.initWorker(context)

        verify(workManager).cancelAllWork()
        verify(workManager).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            getWorkRequest(context)
        )
    }

    @Test
    fun `getWorkRequest() should return a PeriodicWorkRequest`() {
        val pref = mockk<SharedPreferences>()
        val repeatInterval = 5
        val expectedTime = 1*60*60*1000L

        every { context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE) }.returns(pref)
        every { pref.getInt(APIWorker.REFRESH_INTERVAL, 0) }.returns(repeatInterval)

        // Act
        val workRequest = getWorkRequest(context)

        // Assert
        assertEquals(expectedTime, workRequest.workSpec.intervalDuration)
        assertEquals(true, workRequest.workSpec.isPeriodic)
        assertEquals(NetworkType.CONNECTED, workRequest.workSpec.constraints.requiredNetworkType)
        assertEquals(WORK_NAME, workRequest.tags.first())
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}