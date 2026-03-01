package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.favorite.FavoriteDao
import com.example.weatherapp.data.local.favorite.FavoriteLocation
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl(
    private val favoriteDao: FavoriteDao
) : WeatherRepository {

    override fun getFavorites(): Flow<List<FavoriteLocation>> {
        return favoriteDao.getAllFavorites()
    }

    override suspend fun insert(location: FavoriteLocation) {
        favoriteDao.insertFavorite(location)
    }

    override suspend fun delete(location: FavoriteLocation) {
        favoriteDao.delete(location)
    }
}