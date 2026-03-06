package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.favorite.FavoriteLocation
import com.example.weatherapp.data.remote.dto.ForecastResponse
import com.example.weatherapp.data.remote.dto.OneCallDailyResponse
import com.example.weatherapp.data.remote.dto.OneCallResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getFavorites(): Flow<List<FavoriteLocation>>
    suspend fun insert(location: FavoriteLocation)
    suspend fun delete(location: FavoriteLocation)
    suspend fun getCityName(lat: Double, lon: Double): String
    suspend fun getWeather(lat: Double, lon: Double): ForecastResponse
    suspend fun getHourlyForecast(lat: Double, lon: Double): OneCallResponse
    suspend fun getDailyForecast(lat: Double, lon: Double ): OneCallDailyResponse
}