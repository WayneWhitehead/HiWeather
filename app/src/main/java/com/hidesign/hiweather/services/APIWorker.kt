package com.hidesign.hiweather.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.room.Room
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.hidesign.hiweather.database.WeatherDatabase
import com.hidesign.hiweather.model.DbModel
import com.hidesign.hiweather.model.OneCallResponse
import com.hidesign.hiweather.network.ApiClient
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.views.WeatherWidget
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class APIWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("WorkManager", "doWork: ")

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
                "minutely", apiKey, "metric")!!
                .enqueue(object : Callback<OneCallResponse?> {
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
                        } else {
                            return
                        }
                    }

                    override fun onFailure(call: Call<OneCallResponse?>, t: Throwable) {
                        return
                    }
                })

        }
        return Result.success()
    }
}