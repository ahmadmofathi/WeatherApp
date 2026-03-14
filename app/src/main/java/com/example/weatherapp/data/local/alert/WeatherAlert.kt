package com.example.weatherapp.data.local.alert

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_alerts")
data class WeatherAlert(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val latitude: Double,
    val longitude: Double,

    val condition: String,

    val startTime: Long,
    val endTime: Long
)