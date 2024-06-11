package com.example.weathernews

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Weather")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Long,
    val cityName:String,
    val date:String,
    val MaxTemp:String,
    val MinTemp:String
)
