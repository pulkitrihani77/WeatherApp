package com.example.weathernews

data class WeatherApp(
    val address: String,
    val days: List<Day>,
    val latitude: Double,
    val longitude: Double,
    val queryCost: Int,
    val resolvedAddress: String,
    val stations: Stations,
    val timezone: String,
    val tzoffset: Double
)