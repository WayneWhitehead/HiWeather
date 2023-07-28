package com.hidesign.hiweather.services

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.ktx.performance
import com.google.gson.Gson
import com.hidesign.hiweather.R
import com.hidesign.hiweather.database.WeatherDatabase
import com.hidesign.hiweather.model.DbModel
import com.hidesign.hiweather.model.OneCallResponse
import com.hidesign.hiweather.network.WeatherViewModel
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.views.WeatherWidget
import timber.log.Timber
import java.util.concurrent.TimeUnit

class APIWorker(private val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    private val myTrace = Firebase.performance.newTrace("APIWorker")

    override suspend fun doWork(): Result {
        myTrace.start()
        val weatherViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(context as Application)
            .create(WeatherViewModel::class.java)

        val response = weatherViewModel.getBackgroundWeather(context)
        if (response != null && response.isSuccessful) {
            updateWidget(response.body()!!, context)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "channelId"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, "BackgroundNotifications", importance)
            notificationManager.createNotificationChannel(channel)

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.airwaves)
                .setContentTitle(response.body()!!.current.weather[0].description)
                .setContentText("Content")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    notify(1, builder.build())
                }
            }
            myTrace.stop()
            return Result.success()
        } else {
            myTrace.stop()
            return Result.failure()
        }
    }

    companion object {
        private const val WORK_NAME = "com.hidesign.hiweather.services.APIWorker"

        private fun getWorkRequest(repeatInterval: Long): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            return PeriodicWorkRequestBuilder<APIWorker>(repeatInterval, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag(WORK_NAME)
                .build()
        }

        fun createWorkManagerInstance(context: Context) {
            val repeatInterval = context.getSharedPreferences(Constants.preferences, Context.MODE_PRIVATE)
                .getInt(Constants.refreshInterval, 0)
            val timeValue = when (repeatInterval) {
                0 -> 1L
                1 -> 3L
                2 -> 6L
                3 -> 12L
                4 -> 24L
                else -> 1L
            }
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                getWorkRequest(timeValue)
            )
            Timber.d("WorkManager instance created")
        }

        fun updateWidget(weather: OneCallResponse, context: Context) {
            val widgetTrace = Firebase.performance.newTrace("WidgetUpdate")
            widgetTrace.start()

            val jsonWeather: String = Gson().toJson(weather)
            val db = Room.databaseBuilder(context, WeatherDatabase::class.java, "Weather")
                .allowMainThreadQueries().fallbackToDestructiveMigration().build()
            val weatherDao = db.weatherDao()
            weatherDao.insertAll(DbModel(0, jsonWeather))
            val appWidgetManager = AppWidgetManager.getInstance(context)
            for (appWidgetId in appWidgetManager.getAppWidgetIds(ComponentName(context, WeatherWidget::class.java))) {
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.layout.weather_widget)
            }
            widgetTrace.stop()
        }
    }
}