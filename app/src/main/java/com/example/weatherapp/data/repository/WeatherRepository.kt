package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.favorite.FavoriteLocation
import com.example.weatherapp.data.remote.dto.ForecastResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getFavorites(): Flow<List<FavoriteLocation>>
    suspend fun insert(location: FavoriteLocation)
    suspend fun delete(location: FavoriteLocation)
    suspend fun getCityName(lat: Double, lon: Double): String
    suspend fun getWeather(lat: Double, lon: Double): ForecastResponse
}