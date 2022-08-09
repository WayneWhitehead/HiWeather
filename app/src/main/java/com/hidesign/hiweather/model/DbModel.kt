package com.hidesign.hiweather.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DbModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var content: String,
)
