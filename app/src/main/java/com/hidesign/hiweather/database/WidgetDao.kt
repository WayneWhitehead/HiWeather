package com.hidesign.hiweather.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.hidesign.hiweather.model.WeatherWidgetModel

@Dao
interface WidgetDao {
    @Query("SELECT * FROM WeatherWidgetModel")
    fun getAll(): List<WeatherWidgetModel>

    @Insert
    fun insertAll(vararg weather: WeatherWidgetModel)

    @Delete
    fun delete(user: WeatherWidgetModel)
}