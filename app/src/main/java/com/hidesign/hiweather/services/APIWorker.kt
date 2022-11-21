package com.hidesign.hiweather.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.room.Room
import androidx.work.*
import com.google.gson.Gson
import com.hidesign.hiweather.R
import com.hidesign.hiweather.database.WeatherDatabase
import com.hidesign.hiweather.model.DbModel
import com.hidesign.hiweather.model.OneCallResponse
import com.hidesign.hiweather.network.ApiClient
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.views.WeatherWidget
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.concurrent.TimeUnit

class APIWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        Timber.tag("WorkManager").d("doWork: ")
        var result: Result? = null
        val db = Room.databaseBuilder(applicationContext, WeatherDatabase::class.java, "Weather")
            .allowMainThreadQueries().fallbackToDestructiveMigration().build()
        val weatherDao = db.weatherDao()
        val sharedPref =
            applicationContext.getSharedPreferences(Constants.preferences, Context.MODE_PRIVATE)
        if (sharedPref.getFloat(Constants.latitude, 0F) != 0F) {
            val apiKey = Constants.getAPIKey(applicationContext, Constants.openWeatherKey)
            ApiClient().apiService!!.getOneCallWidget(
                sharedPref.getFloat(Constants.latitude, 0F).toDouble(),
                sharedPref.getFloat(Constants.longitude, 0F).toDouble(),
                "minutely", apiKey, "metric")!!.enqueue(object : Callback<OneCallResponse?> {
                override fun onResponse(
                    call: Call<OneCallResponse?>,
                    response: Response<OneCallResponse?>,
                ) {
                    if (response.isSuccessful) {
                        val gson = Gson()
                        val jsonWeather: String = gson.toJson(response.body()!!)
                        weatherDao.insertAll(DbModel(response.body()!!.current.dt, jsonWeather))
                        val alarmIntent = Intent(applicationContext, WeatherWidget::class.java)
                        alarmIntent.action = Constants.auto_update
                        applicationContext.sendBroadcast(alarmIntent)

                        createNotificationChannel()
                        val builder = NotificationCompat.Builder(applicationContext, "Weather")
                            .setSmallIcon(R.drawable.airwaves)
                            .setContentTitle("Weather")
                            .setContentText("Weather")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                        with(NotificationManagerCompat.from(applicationContext)) {
                            // notificationId is a unique int for each notification that you must define
                            notify(1, builder.build())
                        }
                        result = Result.success()
                    } else {
                        result = Result.failure()
                    }
                }

                override fun onFailure(call: Call<OneCallResponse?>, t: Throwable) {
                    result = Result.failure()
                }
            })
        }
        while (result == null) {

        }
        return result!!
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = "Current Weather"
        val descriptionText = "Click here to view weather"
        val importance = NotificationManager.IMPORTANCE_MIN
        val channel = NotificationChannel(1.toString(), name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val WORK_NAME = "com.hidesign.hiweather.services.APIWorker"

        private fun getWorkRequest(repeatInterval: Long): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            return PeriodicWorkRequestBuilder<APIWorker>(repeatInterval, TimeUnit.HOURS)
                .setConstraints(constraints)
                .addTag(WORK_NAME)
                .build()
        }

        fun createWorkManagerInstance(context: Context, repeatInterval: Int) {
            val timeValue = when (repeatInterval) {
                0 -> 1L
                1 -> 3L
                2 -> 6L
                3 -> 12L
                4 -> 24L
                else -> 1L
            }
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                getWorkRequest(timeValue))
        }
    }

}