package com.example.weatherapp.data.repository

import android.util.Log
import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.local.cache.CachedWeather
import com.example.weatherapp.data.local.favorite.FavoriteLocation
import com.example.weatherapp.data.remote.WeatherRemoteDataSource
import com.example.weatherapp.data.remote.dto.ForecastResponse
import com.example.weatherapp.data.remote.dto.GeoLocation
import com.example.weatherapp.data.remote.dto.OneCallDailyResponse
import com.example.weatherapp.data.remote.dto.OneCallResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl(
    private val localDataSource: WeatherLocalDataSource,
    private val remoteDataSource: WeatherRemoteDataSource
) : WeatherRepository {

    private val gson = Gson()

    override fun getFavorites(): Flow<List<FavoriteLocation>> =
        localDataSource.getFavorites()

    override suspend fun insert(location: FavoriteLocation) {
        localDataSource.insertFavorite(location)
    }

    override suspend fun delete(location: FavoriteLocation) {
        localDataSource.deleteFavorite(location)
    }

    override suspend fun getCityName(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): String {
        val response = remoteDataSource.getForecast(lat, lon, unit, lang)
        return response.name
    }

    override suspend fun getWeatherByCity(city: String) =
        remoteDataSource.getWeatherByCity(city, "metric")

    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        unit: String,
        lang: String
    ): ForecastResponse {

        val response = remoteDataSource.getForecast(lat, lon, unit, lang)

        // Cache the successful response
        try {
            val rawJson = gson.toJson(response)
            val cached = CachedWeather(
                id = 1,
                latitude = lat,
                longitude = lon,
                cityName = response.name,
                temperature = response.main.temp,
                description = response.weather.firstOrNull()?.description ?: "",
                humidity = response.main.humidity,
                pressure = response.main.pressure,
                windSpeed = response.wind.speed,
                rawJson = rawJson,
                timestamp = System.currentTimeMillis()
            )
            localDataSource.saveWeather(cached)
        } catch (e: Exception) {
            Log.e("WeatherRepo", "Failed to cache weather", e)
        }

        return response
    }

    override suspend fun getHourlyForecast(
        lat: Double,
        lon: Double,
        unit: String
    ): OneCallResponse {
        return remoteDataSource.getHourlyForecast(lat, lon, unit)
    }

    override suspend fun getDailyForecast(
        lat: Double,
        lon: Double,
        unit: String
    ): OneCallDailyResponse {
        return remoteDataSource.getDailyForecast(lat, lon, unit)
    }

    override suspend fun searchCity(city: String): List<GeoLocation> {
        return remoteDataSource.searchCity(city)
    }

    override suspend fun getCachedWeather(): ForecastResponse? {
        return try {
            val cached = localDataSource.getLastWeather() ?: return null
            gson.fromJson(cached.rawJson, ForecastResponse::class.java)
        } catch (e: Exception) {
            Log.e("WeatherRepo", "Failed to read cached weather", e)
            null
        }
    }
}