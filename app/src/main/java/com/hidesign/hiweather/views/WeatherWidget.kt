package com.hidesign.hiweather.views

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import android.widget.RemoteViews
import androidx.room.Room
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.AppWidgetTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.ktx.performance
import com.google.gson.Gson
import com.hidesign.hiweather.R
import com.hidesign.hiweather.database.WeatherDatabase
import com.hidesign.hiweather.model.DbModel
import com.hidesign.hiweather.model.OneCallResponse
import com.hidesign.hiweather.services.APIWorker
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.util.DateUtils
import com.hidesign.hiweather.util.WeatherUtils
import timber.log.Timber
import java.text.MessageFormat
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

open class WeatherWidget : AppWidgetProvider() {
    private val myTrace = Firebase.performance.newTrace("WidgetRefreshed")

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        val timeValue = DateUtils.getRefreshInterval(context)
        val periodicWorkRequest = PeriodicWorkRequest.Builder(APIWorker::class.java,
            timeValue,
            TimeUnit.MINUTES,
            timeValue,
            TimeUnit.MINUTES).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("APIWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWorkRequest)
    }

    override fun onDisabled(context: Context) {
        WorkManager.getInstance(context).cancelAllWork()
    }

    override fun onReceive(context: Context?, intent: Intent) {
        super.onReceive(context, intent)
        if (Constants.refresh == intent.action) {
            myTrace.start()
            val appWidgetManager = AppWidgetManager.getInstance(context)
            updateAppWidget(context!!, appWidgetManager, intent.getIntExtra("appWidgetId", 0))
        }
        if (Constants.auto_update == intent.action) {
            myTrace.start()
            val appWidgetManager = AppWidgetManager.getInstance(context)
            updateAppWidget(context!!, appWidgetManager, intent.getIntExtra("appWidgetId", 0))
        }
    }

    protected open fun getPendingSelfIntent(
        context: Context?,
        action: String?,
        appWidgetId: Int,
    ): PendingIntent? {
        val intent = Intent(context, this.javaClass)
        intent.action = action
        intent.putExtra("appWidgetId", appWidgetId)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
    ) {
        val db = Room.databaseBuilder(context, WeatherDatabase::class.java, "Weather")
            .allowMainThreadQueries().fallbackToDestructiveMigration().build()
        val widgetDao = db.weatherDao()
        if (widgetDao.getAll().isNotEmpty()) {
            val weatherContent = widgetDao.getAll().last()
            updateViews(context,
                weatherContent,
                appWidgetManager,
                appWidgetId)
        }
    }

    private fun updateViews(
        context: Context,
        weather: DbModel,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
    ) {
        val weatherContent = Gson().fromJson(weather.content, OneCallResponse::class.java)
        val views = RemoteViews(context.packageName, R.layout.weather_widget)

        val awt: AppWidgetTarget =
            object : AppWidgetTarget(context.applicationContext, R.id.image, views, appWidgetId) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    super.onResourceReady(resource, transition)
                    views.setViewVisibility(R.id.image, View.VISIBLE)
                }
            }
        Glide.with(context.applicationContext).asBitmap().load(WeatherUtils.getWeatherIconUrl(
            weatherContent.current.weather[0].icon)).apply(RequestOptions().override(30, 30))
            .into(awt)

        views.setTextViewText(R.id.date, DateUtils.getDateTime("dd/MM HH:mm",
            weatherContent.current.dt.toLong(),
            weatherContent.timezone))
        val currentTemp = weatherContent.current.temp.roundToInt()
        views.setTextViewText(R.id.current_temp,
            MessageFormat.format(context.getString(R.string._0_c), currentTemp))
        val realFeel = weatherContent.current.feelsLike.roundToInt()
        views.setTextViewText(R.id.real_feel_temp,
            MessageFormat.format(context.getString(R.string.real_feel_0_c), realFeel))
        val uvi = weatherContent.current.uvi.roundToInt()
        views.setTextViewText(R.id.uv_index,
            MessageFormat.format(context.getString(R.string.uv_index_0), uvi))
        val precipitation = (weatherContent.hourly[0].pop * 100)
        views.setTextViewText(R.id.precipitation,
            MessageFormat.format(context.getString(R.string._0_p), precipitation))
        val humidity = weatherContent.current.humidity
        views.setTextViewText(R.id.humidity,
            MessageFormat.format(context.getString(R.string._0_p), humidity))
        val cloudiness = (weatherContent.current.clouds)
        views.setTextViewText(R.id.cloudiness,
            MessageFormat.format(context.getString(R.string._0_p), cloudiness))

        views.setOnClickPendingIntent(R.id.layout,
            getPendingSelfIntent(context, Constants.refresh, appWidgetId))

        appWidgetManager.updateAppWidget(appWidgetId, views)
        myTrace.stop()
        Timber.tag("WeatherWidget").d("updateViews")
    }
}