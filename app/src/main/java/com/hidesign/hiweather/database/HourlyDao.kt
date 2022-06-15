package com.hidesign.hiweather.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.hidesign.hiweather.model.Hourly

@Dao
interface HourlyDao {
    @Query("SELECT * FROM hourly")
    fun getAll(): List<Hourly>

    @Insert
    fun insertAll(vararg weather: Hourly)

    @Delete
    fun delete(user: Hourly)
}