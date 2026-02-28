package com.example.weatherapp.data.remote

import retrofit2.http.Query
import com.example.weatherapp.data.remote.dto.ForecastResponse
import com.example.weatherapp.utils.Constants
import retrofit2.http.GET

interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("appid") apiKey: String = Constants.API_KEY
    ): ForecastResponse
}