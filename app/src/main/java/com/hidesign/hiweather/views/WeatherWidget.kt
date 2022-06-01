package com.hidesign.hiweather.views

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.widget.RemoteViews
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.hidesign.hiweather.R
import com.hidesign.hiweather.model.Daily
import com.hidesign.hiweather.network.WeatherViewModel
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.util.WeatherUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class WeatherWidget : AppWidgetProvider(), CoroutineScope, LifecycleObserver, ViewModelStoreOwner {

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        // There may be multiple widgets active, so update all of them
        val sharedPreferences = context.getSharedPreferences(Constants.userPreferences, 0)
        val latitude = sharedPreferences.getFloat(Constants.userLongitude, 0F).toDouble()
        val longitude = sharedPreferences.getFloat(Constants.userLongitude, 0F).toDouble()
        val weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        launch {
            val ai: ApplicationInfo = context.packageManager.getApplicationInfo(context.packageName,
                PackageManager.GET_META_DATA)
            val value = ai.metaData["weatherKey"]
            val apiKey = value.toString()

            val oneCallResponse = weatherViewModel.getOneCallWeather(latitude, longitude, apiKey)
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context,
                    appWidgetManager,
                    appWidgetId,
                    oneCallResponse!!.body()!!.daily[0])
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        weatherDaily: Daily,
    ) {
        val views = RemoteViews(context.packageName, R.layout.weather_widget)

        views.setTextViewText(R.id.CurrentTemp, weatherDaily.temp.day.toString() + "°C")
        views.setTextViewText(R.id.RealFeelTemp, weatherDaily.feelsLike.toString() + "°C")
        views.setTextViewText(R.id.LowTemp, weatherDaily.temp.min.toString() + "°C")
        views.setTextViewText(R.id.HighTemp, weatherDaily.temp.max.toString() + "°C")

        views.setTextViewText(R.id.WindSpeed, weatherDaily.windSpeed.toString())
        views.setTextViewText(R.id.WindSpeed, WeatherUtils.getWindDegreeText(weatherDaily.windDeg))
        views.setImageViewResource(R.id.skiesImage,
            WeatherUtils.getWeatherIcon(weatherDaily.clouds))
        views.setOnClickPendingIntent(R.id.currentInfo,
            PendingIntent.getActivity(context, 0, Intent(context,
                WeatherActivity::class.java), 0))

        //binding.WindDirectionDegrees.rotation = ((weatherDaily!!.windDeg - 270).toFloat())

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun getViewModelStore(): ViewModelStore {
        return ViewModelStore()
    }
}