package com.hidesign.hiweather.views

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import androidx.room.Room
import com.hidesign.hiweather.R
import com.hidesign.hiweather.database.WeatherDatabase
import com.hidesign.hiweather.util.DateUtils
import java.text.MessageFormat
import kotlin.math.roundToInt


class WeatherWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        for (appWidgetId in appWidgetIds) {
            val db = Room.databaseBuilder(context, WeatherDatabase::class.java, "Weather")
                .allowMainThreadQueries().build()
            val hourlyDao = db.hourlyDao()
            val weatherHourly = hourlyDao.getAll()[0]
            val views = RemoteViews(context.packageName, R.layout.weather_widget)

            views.setTextViewText(R.id.date,
                DateUtils.getDateTime("d MMMM HH:mm",
                    weatherHourly.dt?.toLong()!!,
                    weatherHourly.timezone!!))
            val currentTemp = weatherHourly.temp?.roundToInt() ?: 0
            views.setTextViewText(R.id.current_temp,
                MessageFormat.format(context.getString(R.string._0_c), currentTemp))
            val realFeel = (weatherHourly.feelsLike ?: 0.0).roundToInt()
            views.setTextViewText(R.id.real_feel_temp,
                MessageFormat.format(context.getString(R.string.real_feel_0_c), realFeel))
            val uvi = (weatherHourly.uvi ?: 0.0).roundToInt()
            views.setTextViewText(R.id.uv_index,
                MessageFormat.format(context.getString(R.string.uv_index_0), uvi))

            val precipitation = ((weatherHourly.pop ?: 0.0) * 100)
            views.setTextViewText(R.id.precipitation,
                MessageFormat.format(context.getString(R.string._0_p), precipitation))
            val humidity = weatherHourly.humidity ?: 0
            views.setTextViewText(R.id.humidity,
                MessageFormat.format(context.getString(R.string._0_p), humidity))
            val cloudiness = (weatherHourly.clouds) ?: 0
            views.setTextViewText(R.id.cloudiness, cloudiness.toString())
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}