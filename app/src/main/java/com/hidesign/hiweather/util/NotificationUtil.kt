package com.hidesign.hiweather.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.hidesign.hiweather.R
import com.hidesign.hiweather.data.model.AirPollutionResponse
import com.hidesign.hiweather.data.model.OneCallResponse
import java.util.Calendar
import kotlin.math.roundToInt

object NotificationUtil {
    fun createOrUpdateNotification(context: Context, notificationData: NotificationData, notificationId: Int) {
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
            .setContentTitle(notificationData.title)
            .setContentText(notificationData.body)
            .setAllowSystemGeneratedContextualActions(true)
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

    fun createWeatherNotificationData(interval: Int, oneCallResponse: OneCallResponse?, locality: String): NotificationData? {
        oneCallResponse?.let { response ->
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            return when (interval) {
                1, 2 -> NotificationData(
                    title = "${response.current.temp.roundToInt()}° in $locality",
                    body = "Feels like: ${response.current.feelsLike.roundToInt()}° " +
                            "with ${response.current.weather.first().description}.\n"
                )
                3, 4 -> NotificationData(
                    title = "${WeatherUtil.getTempOfDay(currentHour, response.daily.first().temp)}° in $locality",
                    body = "Feels like: ${WeatherUtil.getFeelsLikeOfDay(currentHour, response.daily.first().feelsLike)}° " +
                            "with ${response.current.weather.first().description}.\n"
                )
                5 -> NotificationData(
                        title = "${WeatherUtil.getTempOfDay(currentHour, response.daily.first().temp)}° in $locality",
                        body = response.daily[0].summary
                )
                else -> NotificationData(
                    title = "A high of ${response.daily[0].temp.max.roundToInt()}° and a low of ${response.daily[0].temp.min.roundToInt()}° in $locality" ,
                    body = "Today's weather: ${response.daily[0].summary}. \n" +
                            "Tomorrow's forecast: ${response.daily[1].summary}"
                )
            }
        } ?: return null
    }

    fun createAirPollutionNotificationData(airPollutionResponse: AirPollutionResponse?, locality: String): NotificationData? {
        airPollutionResponse?.let { response ->
            return NotificationData(
                title = "${5 - response.list.first().main.aqi}/5 Air Quality in $locality",
                body = "CO: ${response.list.first().components.co} \n" +
                        "NH3: ${response.list.first().components.nh3} \n" +
                        "NO: ${response.list.first().components.no} \n" +
                        "NO2: ${response.list.first().components.no2} \n" +
                        "O3: ${response.list.first().components.o3} \n" +
                        "PM10: ${response.list.first().components.pm10} \n" +
                        "PM25: ${response.list.first().components.pm25} \n" +
                        "SO2: ${response.list.first().components.so2}"
            )
        } ?: return null
    }

    data class NotificationData (
        val title: String,
        val body: String
    )
}