package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.alert.AlertDao
import com.example.weatherapp.data.local.alert.WeatherAlert
import kotlinx.coroutines.flow.Flow

class AlertRepository(
    private val dao: AlertDao
) {

    suspend fun insertAlert(alert: WeatherAlert): Long {
        return dao.insertAlert(alert)
    }

    suspend fun deleteAlert(alert: WeatherAlert) {
        dao.deleteAlert(alert)
    }

    suspend fun updateAlert(alert: WeatherAlert) {
        dao.updateAlert(alert)
    }

    fun getAlerts(): Flow<List<WeatherAlert>> {
        return dao.getAlerts()
    }

    suspend fun getAlertById(id: Int): WeatherAlert? {
        return dao.getAlertById(id)
    }
}