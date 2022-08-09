package com.hidesign.hiweather.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.hidesign.hiweather.model.DbModel

@Dao
interface WeatherDao {
    @Query("SELECT * FROM DbModel")
    fun getAll(): List<DbModel>

    @Insert
    fun insertAll(vararg weather: DbModel)

    @Delete
    fun delete(user: DbModel)
}