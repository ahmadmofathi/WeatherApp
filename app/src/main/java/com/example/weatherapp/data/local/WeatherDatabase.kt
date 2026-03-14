package com.example.weatherapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherapp.data.local.alert.*
import com.example.weatherapp.data.local.cache.*
import com.example.weatherapp.data.local.favorite.*


@Database(
    entities = [
        FavoriteLocation::class,
        WeatherAlert::class,
        CachedWeather::class
    ],
    version = 4
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao

    abstract fun alertDao(): AlertDao

    abstract fun cachedWeatherDao(): CachedWeatherDao
}
