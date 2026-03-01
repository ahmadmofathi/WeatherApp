package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.favorite.FavoriteLocation
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getFavorites(): Flow<List<FavoriteLocation>>
    suspend fun insert(location: FavoriteLocation)
    suspend fun delete(location: FavoriteLocation)
}