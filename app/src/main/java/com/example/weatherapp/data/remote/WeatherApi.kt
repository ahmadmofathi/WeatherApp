package com.example.weatherapp.data.remote

import retrofit2.http.Query
import com.example.weatherapp.data.remote.dto.ForecastResponse
import com.example.weatherapp.data.remote.dto.OneCallDailyResponse
import com.example.weatherapp.data.remote.dto.OneCallResponse
import com.example.weatherapp.utils.Constants
import retrofit2.http.GET

interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "en",
        @Query("appid") apiKey: String = Constants.API_KEY
    ): ForecastResponse


    @GET("data/3.0/onecall")
    suspend fun getHourlyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("exclude") exclude: String = "minutely,daily,alerts",
        @Query("appid") apiKey: String = Constants.API_KEY
    ): OneCallResponse

    @GET("data/3.0/onecall")
    suspend fun getDailyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("exclude") exclude: String = "minutely,hourly,alerts",
        @Query("appid") apiKey: String = Constants.API_KEY
    ): OneCallDailyResponse
}