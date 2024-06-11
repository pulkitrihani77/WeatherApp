package com.example.weathernews

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WeatherEntity::class], version = 1)
abstract class weatherDatabase:RoomDatabase() {
    abstract fun WeatherDao():WeatherDao
}