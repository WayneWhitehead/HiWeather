package com.hidesign.hiweather.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hidesign.hiweather.model.DbModel

@Database(
    entities = [DbModel::class],
    version = 3)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}