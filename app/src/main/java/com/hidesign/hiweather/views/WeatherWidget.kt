package com.hidesign.hiweather.views

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.AppWidgetTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.ktx.performance
import com.hidesign.hiweather.R
import com.hidesign.hiweather.database.WeatherDatabase
import com.hidesign.hiweather.model.OneCallResponse
import com.hidesign.hiweather.model.WeatherWidgetModel
import com.hidesign.hiweather.network.ApiClient
import com.hidesign.hiweather.util.Constants
import com.hidesign.hiweather.util.DateUtils
import com.hidesign.hiweather.util.WeatherUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.MessageFormat
import java.util.*
import kotlin.math.roundToInt

open class WeatherWidget : AppWidgetProvider() {
    private val REFRESH = "refreshButtonClick"
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

    override fun onReceive(context: Context?, intent: Intent) {
        super.onReceive(context, intent)
        if (REFRESH == intent.action) {
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
        var weatherContent: WeatherWidgetModel
        val db = Room.databaseBuilder(context, WeatherDatabase::class.java, "Weather")
            .allowMainThreadQueries().fallbackToDestructiveMigration().build()
        val widgetDao = db.widgetDao()
        if (widgetDao.getAll().isNotEmpty()) {
            weatherContent = widgetDao.getAll().last()
            val difference =
                Calendar.getInstance().timeInMillis - (weatherContent.dt!!.toLong() * 1000)
            if (difference > 1) {
                val sharedPref =
                    context.getSharedPreferences(Constants.preferences, Context.MODE_PRIVATE)
                if (sharedPref.getFloat(Constants.latitude, 0F) != 0F) {
                    val apiKey = Constants.getAPIKey(context, Constants.openWeatherKey)
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
                                    weatherContent =
                                        WeatherUtils.createWidgetModel(response.body()!!)
                                    var found = false
                                    db.widgetDao().getAll().forEach {
                                        if (it.dt == weatherContent.dt) {
                                            found = true
                                        }
                                    }
                                    if (!found) {
                                        updateViews(context,
                                            weatherContent,
                                            appWidgetManager,
                                            appWidgetId)
                                        return
                                    }
                                } else {
                                    return
                                }
                            }

                            override fun onFailure(call: Call<OneCallResponse?>, t: Throwable) {
                                return
                            }
                        })
                }
            }
            updateViews(context, weatherContent, appWidgetManager, appWidgetId)
            return
        } else {
            return
        }
    }

    fun updateViews(
        context: Context,
        weatherHourly: WeatherWidgetModel,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
    ) {
        val views = RemoteViews(context.packageName, R.layout.weather_widget)

        val awt: AppWidgetTarget =
            object : AppWidgetTarget(context.applicationContext, R.id.image, views, appWidgetId) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    super.onResourceReady(resource, transition)
                    views.setViewVisibility(R.id.image, View.VISIBLE)
                }
            }
        Glide.with(context.applicationContext).asBitmap().load(WeatherUtils.getWeatherIconUrl(
            weatherHourly.icon!!)).apply(RequestOptions().override(30, 30)).into(awt)

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
        views.setTextViewText(R.id.cloudiness,
            MessageFormat.format(context.getString(R.string._0_p), cloudiness))

        views.setOnClickPendingIntent(R.id.layout,
            getPendingSelfIntent(context, REFRESH, appWidgetId))

        appWidgetManager.updateAppWidget(appWidgetId, views)
        myTrace.stop()
        Log.e("WeatherWidget", "updateViews: ")
    }
}