package com.example.weatherapp.data.local.cache

import androidx.room.*

@Dao
interface CachedWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWeather(cache: CachedWeather)

    @Query("SELECT * FROM cached_weather WHERE id = 1")
    suspend fun getLastWeather(): CachedWeather?
}
