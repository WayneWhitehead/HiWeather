package com.hidesign.hiweather.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hidesign.hiweather.model.Hourly

@Database(
    entities = [Hourly::class],
    version = 5,
    exportSchema = true)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun hourlyDao(): HourlyDao
}