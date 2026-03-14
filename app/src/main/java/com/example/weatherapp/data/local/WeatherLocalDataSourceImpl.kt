package com.example.weatherapp.data.local

import com.example.weatherapp.data.local.cache.CachedWeather
import com.example.weatherapp.data.local.cache.CachedWeatherDao
import com.example.weatherapp.data.local.favorite.FavoriteDao
import com.example.weatherapp.data.local.favorite.FavoriteLocation
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceImpl(
    private val favoriteDao: FavoriteDao,
    private val cachedWeatherDao: CachedWeatherDao
) : WeatherLocalDataSource {

    override fun getFavorites(): Flow<List<FavoriteLocation>> =
        favoriteDao.getAllFavorites()

    override suspend fun insertFavorite(location: FavoriteLocation) {
        favoriteDao.insertFavorite(location)
    }

    override suspend fun deleteFavorite(location: FavoriteLocation) {
        favoriteDao.delete(location)
    }

    override suspend fun saveWeather(cache: CachedWeather) {
        cachedWeatherDao.saveWeather(cache)
    }

    override suspend fun getLastWeather(): CachedWeather? {
        return cachedWeatherDao.getLastWeather()
    }
}
