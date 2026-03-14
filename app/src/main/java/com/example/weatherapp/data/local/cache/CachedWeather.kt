package com.example.weatherapp.data.local.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_weather")
data class CachedWeather(

    @PrimaryKey
    val id: Int = 1,

    val latitude: Double,
    val longitude: Double,
    val cityName: String,

    val temperature: Double,
    val description: String,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,

    val rawJson: String,

    val timestamp: Long
)
