package com.example.weatherapp.data.remote

import com.example.weatherapp.data.remote.dto.ForecastResponse
import com.example.weatherapp.data.remote.dto.GeoLocation
import com.example.weatherapp.data.remote.dto.OneCallDailyResponse
import com.example.weatherapp.data.remote.dto.OneCallResponse

interface WeatherRemoteDataSource {

    suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): ForecastResponse

    suspend fun getWeatherByCity(
        city: String,
        units: String
    ): ForecastResponse

    suspend fun getHourlyForecast(
        lat: Double,
        lon: Double,
        units: String
    ): OneCallResponse

    suspend fun getDailyForecast(
        lat: Double,
        lon: Double,
        units: String
    ): OneCallDailyResponse

    suspend fun searchCity(city: String): List<GeoLocation>
}
