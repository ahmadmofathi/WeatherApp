package com.example.weatherapp.fakes

import com.example.weatherapp.data.local.favorite.FavoriteLocation
import com.example.weatherapp.data.remote.dto.*
import com.example.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeWeatherRepository : WeatherRepository {

    private val favorites = mutableListOf<FavoriteLocation>()
    private val favoritesFlow = MutableStateFlow<List<FavoriteLocation>>(emptyList())

    var forecastResponse: ForecastResponse? = null
    var hourlyResponse: OneCallResponse? = null
    var dailyResponse: OneCallDailyResponse? = null
    var cachedResponse: ForecastResponse? = null
    var shouldThrow: Boolean = false

    // Track calls for verification
    var getWeatherCallCount = 0
    var lastGetWeatherLat: Double? = null
    var lastGetWeatherLon: Double? = null

    override fun getFavorites(): Flow<List<FavoriteLocation>> = favoritesFlow

    override suspend fun insert(location: FavoriteLocation) {
        favorites.add(location)
        favoritesFlow.value = favorites.toList()
    }

    override suspend fun delete(location: FavoriteLocation) {
        favorites.removeAll { it.cityName == location.cityName }
        favoritesFlow.value = favorites.toList()
    }

    override suspend fun getCityName(
        lat: Double, lon: Double, unit: String, lang: String
    ): String {
        if (shouldThrow) throw Exception("Network error")
        return forecastResponse?.name ?: "Unknown"
    }

    override suspend fun getWeather(
        lat: Double, lon: Double, unit: String, lang: String
    ): ForecastResponse {
        getWeatherCallCount++
        lastGetWeatherLat = lat
        lastGetWeatherLon = lon
        if (shouldThrow) throw Exception("Network error")
        return forecastResponse ?: throw Exception("No data")
    }

    override suspend fun getHourlyForecast(
        lat: Double, lon: Double, unit: String
    ): OneCallResponse {
        if (shouldThrow) throw Exception("Network error")
        return hourlyResponse ?: throw Exception("No data")
    }

    override suspend fun getDailyForecast(
        lat: Double, lon: Double, unit: String
    ): OneCallDailyResponse {
        if (shouldThrow) throw Exception("Network error")
        return dailyResponse ?: throw Exception("No data")
    }

    override suspend fun getWeatherByCity(city: String): ForecastResponse {
        if (shouldThrow) throw Exception("Network error")
        return forecastResponse ?: throw Exception("No data")
    }

    override suspend fun searchCity(city: String): List<GeoLocation> {
        return emptyList()
    }

    override suspend fun getCachedWeather(): ForecastResponse? {
        return cachedResponse
    }

    fun getFavoritesList(): List<FavoriteLocation> = favorites.toList()
}
