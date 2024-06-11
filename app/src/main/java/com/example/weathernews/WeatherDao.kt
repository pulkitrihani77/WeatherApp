package com.example.weathernews

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

data class ResultData(
    val MaxTemp: String,
    val MinTemp: String
)

@Dao
interface WeatherDao {
    @Insert
    suspend fun insert(weatherEntity:WeatherEntity)

    @Query("SELECT MaxTemp, MinTemp FROM Weather WHERE cityName = :cityName AND date = :date")
    suspend fun getResult(cityName: String, date: String): ResultData?

}
