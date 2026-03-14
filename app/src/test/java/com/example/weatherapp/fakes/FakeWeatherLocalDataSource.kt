package com.example.weatherapp.fakes

import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.local.cache.CachedWeather
import com.example.weatherapp.data.local.favorite.FavoriteLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeWeatherLocalDataSource : WeatherLocalDataSource {

    private val favorites = mutableListOf<FavoriteLocation>()
    private val favoritesFlow = MutableStateFlow<List<FavoriteLocation>>(emptyList())

    var cachedWeather: CachedWeather? = null
    var savedCache: CachedWeather? = null

    override fun getFavorites(): Flow<List<FavoriteLocation>> = favoritesFlow

    override suspend fun insertFavorite(location: FavoriteLocation) {
        favorites.add(location)
        favoritesFlow.value = favorites.toList()
    }

    override suspend fun deleteFavorite(location: FavoriteLocation) {
        favorites.removeAll { it.cityName == location.cityName }
        favoritesFlow.value = favorites.toList()
    }

    override suspend fun saveWeather(cache: CachedWeather) {
        savedCache = cache
        cachedWeather = cache
    }

    override suspend fun getLastWeather(): CachedWeather? {
        return cachedWeather
    }

    fun getFavoritesList(): List<FavoriteLocation> = favorites.toList()
}
