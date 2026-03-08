package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.favorite.FavoriteDao
import com.example.weatherapp.data.local.favorite.FavoriteLocation
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.remote.dto.OneCallDailyResponse
import com.example.weatherapp.data.remote.dto.OneCallResponse
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl(
    private val favoriteDao: FavoriteDao,
    private val api: WeatherApi
) : WeatherRepository {

    override fun getFavorites(): Flow<List<FavoriteLocation>> =
        favoriteDao.getAllFavorites()

    override suspend fun insert(location: FavoriteLocation) {
        favoriteDao.insertFavorite(location)
    }

    override suspend fun delete(location: FavoriteLocation) {
        favoriteDao.delete(location)
    }

    override suspend fun getCityName(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): String {

        val response =
            api.getForecast(lat, lon, unit, lang)

        return response.name
    }

    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ) =
        api.getForecast(lat, lon, unit, lang)

    override suspend fun getHourlyForecast(
        lat: Double,
        lon: Double,
        unit: String
    ): OneCallResponse {

        return api.getHourlyForecast(lat, lon, unit)
    }

    override suspend fun getDailyForecast(
        lat: Double,
        lon: Double,
        unit: String
    ): OneCallDailyResponse {

        return api.getDailyForecast(lat, lon, unit)
    }
}