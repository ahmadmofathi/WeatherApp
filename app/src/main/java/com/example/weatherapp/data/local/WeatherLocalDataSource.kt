package com.example.weatherapp.data.local

import com.example.weatherapp.data.local.cache.CachedWeather
import com.example.weatherapp.data.local.favorite.FavoriteLocation
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {

    // Favorites
    fun getFavorites(): Flow<List<FavoriteLocation>>
    suspend fun insertFavorite(location: FavoriteLocation)
    suspend fun deleteFavorite(location: FavoriteLocation)

    // Cache
    suspend fun saveWeather(cache: CachedWeather)
    suspend fun getLastWeather(): CachedWeather?
}
