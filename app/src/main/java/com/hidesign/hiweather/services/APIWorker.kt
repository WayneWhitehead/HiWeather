package com.hidesign.hiweather.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.location.Address
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.hidesign.hiweather.R
import com.hidesign.hiweather.model.OneCallResponse
import com.hidesign.hiweather.network.WeatherRepository
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.views.WeatherWidget
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.scopes.ViewModelScoped
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


@HiltWorker
class APIWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    @ViewModelScoped val weatherRepository: WeatherRepository,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val pref = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
        val lat = pref.getString(Constants.LATITUDE, "0.0")?.toDouble() ?: 0.0
        val lon = pref.getString(Constants.LONGITUDE, "0.0")?.toDouble() ?: 0.0
        val locality = pref.getString(Constants.LOCALITY, "") ?: ""
        val units = Constants.getUnit(context)

        var response by mutableStateOf(OneCallResponse())
        val weatherResponse = weatherRepository.getWeather(
            address = Address(Locale.getDefault()).apply {
                latitude = lat
                longitude = lon
            },
            unit = units
        )
        weatherResponse.onSuccess {
            if (it != null) {
                response = it
            }
        }
        weatherResponse.onFailure {
            return Result.failure()
        }

        val prefs = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
        prefs.edit().putString(Constants.WEATHER_RESPONSE, Gson().toJson(response)).apply()
        updateWidget(context)

        createOrUpdateNotification(locality, response)
        return Result.success()
    }

    private fun createOrUpdateNotification(locality: String, response: OneCallResponse) {
        val notificationId = 1
        val channelId = "my_channel_id"
        val channelName = "Background Weather"
        val channelImportance = NotificationManager.IMPORTANCE_DEFAULT

        val notificationChannel = NotificationChannel(channelId, channelName, channelImportance)
        notificationChannel.description = "Notifications for background weather updates"

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("${response.current?.temp?.roundToInt()}° in $locality")
            .setContentText(response.daily[0].summary)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(notificationId, notification)
        } else {
            notificationManager.cancel(notificationId)
        }
    }

    companion object {
        private const val WORK_NAME = "com.hidesign.hiweather.services.APIWorker"

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

        private fun getWorkRequest(context: Context): PeriodicWorkRequest {
            val pref = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
            val repeatInterval = pref.getInt(Constants.REFRESH_INTERVAL, 0)
            val timeValue = when (repeatInterval) {
                0 -> 1L
                1 -> 3L
                2 -> 6L
                3 -> 12L
                4 -> 24L
                else -> 1L
            }
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            return PeriodicWorkRequestBuilder<APIWorker>(timeValue, TimeUnit.HOURS)
                .setConstraints(constraints)
                .addTag(WORK_NAME)
                .build()
        }

        fun updateWidget(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            for (appWidgetId in appWidgetManager.getAppWidgetIds(ComponentName(context, WeatherWidget::class.java))) {
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.layout.weather_widget)
            }
        }
    }
}