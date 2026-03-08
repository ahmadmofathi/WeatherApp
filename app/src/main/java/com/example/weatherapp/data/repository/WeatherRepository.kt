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
    suspend fun getCityName(lat: Double, lon: Double, unit: String, lang: String): String
    suspend fun getWeather(lat: Double, lon: Double, unit: String, lang: String): ForecastResponse
    suspend fun getHourlyForecast(lat: Double, lon: Double, unit: String): OneCallResponse
    suspend fun getDailyForecast(lat: Double, lon: Double, unit: String): OneCallDailyResponse
}