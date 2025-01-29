package com.hidesign.hiweather.services

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.location.Address
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.hidesign.hiweather.R
import com.hidesign.hiweather.domain.usecase.GetAirPollutionUseCase
import com.hidesign.hiweather.domain.usecase.GetOneCallUseCase
import com.hidesign.hiweather.presentation.WeatherWidget
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.util.NotificationUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.scopes.ViewModelScoped
import java.util.Locale
import java.util.concurrent.TimeUnit

@HiltWorker
class APIWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    @ViewModelScoped val getOneCallUseCase: GetOneCallUseCase,
    @ViewModelScoped val getAirPollutionUseCase: GetAirPollutionUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val pref = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
        val address = Address(Locale.getDefault()).apply {
            latitude = pref.getFloat(Constants.LATITUDE, 0.0F).toDouble()
            longitude = pref.getFloat(Constants.LONGITUDE, 0.0F).toDouble()
        }
        val locality = pref.getString(Constants.LOCALITY, "") ?: ""

        if (pref.getBoolean(WEATHER_UPDATES, false)) {
            getOneCallUseCase(address).collect { result ->
                result.fold(
                    onSuccess = { oneCallResponse ->
                        NotificationUtil.createWeatherNotificationData(
                            interval = pref.getInt(REFRESH_INTERVAL, 0),
                            oneCallResponse = oneCallResponse,
                            locality = locality
                        )?.let {
                            NotificationUtil.createOrUpdateNotification(context, it, WEATHER_UPDATES_ID)
                        } ?: Result.failure()
                    },
                    onFailure = {
                        Result.failure()
                    }
                )
            }
        }
        if (pref.getBoolean(AIR_UPDATES, false)) {
            getAirPollutionUseCase(address).collect { result ->
                result.fold(
                    onSuccess = { airPollutionResponse ->
                        NotificationUtil.createAirPollutionNotificationData(
                            airPollutionResponse = airPollutionResponse,
                            locality = locality
                        )?.let {
                            NotificationUtil.createOrUpdateNotification(context, it, AIR_UPDATES_ID)
                        } ?: Result.failure()
                    },
                    onFailure = { Result.failure() }
                )
            }
        }

        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "com.hidesign.hiweather.services.APIWorker"
        const val WEATHER_UPDATES = "weatherUpdates"
        const val AIR_UPDATES = "airUpdates"
        const val REFRESH_INTERVAL = "refreshInterval"
        private const val WEATHER_UPDATES_ID = 1
        private const val AIR_UPDATES_ID = 2

        fun initWorker(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWork()
            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                getWorkRequest(context)
            )
        }

        fun cancelWorker(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWorkByTag(WORK_NAME)
        }

        fun updateWidget(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            for (appWidgetId in appWidgetManager.getAppWidgetIds(ComponentName(context, WeatherWidget::class.java))) {
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.layout.weather_widget)
            }
        }

        private fun getWorkRequest(context: Context): PeriodicWorkRequest {
            val pref = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
            val repeatInterval = pref.getInt(REFRESH_INTERVAL, 4)
            val timeValue = when (repeatInterval) {
                1 -> 1L
                2 -> 2L
                3 -> 4L
                4 -> 6L
                5 -> 12L
                6 -> 24L
                else -> 0L
            }
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            return PeriodicWorkRequestBuilder<APIWorker>(timeValue, TimeUnit.HOURS)
                .setConstraints(constraints)
                .addTag(WORK_NAME)
                .build()
        }
    }
}