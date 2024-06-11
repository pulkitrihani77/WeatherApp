package com.example.weathernews

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @GET("VisualCrossingWebServices/rest/services/timeline/{city}/{date}")
    fun getAPI(
        @Path("city") city: String,
        @Path("date") date: String,
        @Query("key") apiKey: String,
        @Query("include") include: String = "current"
    ): Call<WeatherApp>
}