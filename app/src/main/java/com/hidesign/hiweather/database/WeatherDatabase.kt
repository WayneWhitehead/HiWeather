package com.hidesign.hiweather.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hidesign.hiweather.model.WeatherWidgetModel

@Database(
    entities = [WeatherWidgetModel::class],
    version = 1,
    exportSchema = true)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun widgetDao(): WidgetDao
}