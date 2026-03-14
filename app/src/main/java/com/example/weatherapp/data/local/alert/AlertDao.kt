package com.example.weatherapp.data.local.alert

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    @Insert
    suspend fun insertAlert(alert: WeatherAlert): Long

    @Delete
    suspend fun deleteAlert(alert: WeatherAlert)

    @Update
    suspend fun updateAlert(alert: WeatherAlert)

    @Query("SELECT * FROM weather_alerts")
    fun getAlerts(): Flow<List<WeatherAlert>>

    @Query("SELECT * FROM weather_alerts WHERE id = :id")
    suspend fun getAlertById(id: Int): WeatherAlert?
}