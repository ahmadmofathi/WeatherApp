package com.example.weatherapp.data.preferences

import kotlinx.coroutines.flow.Flow

/**
 * Interface for settings data access.
 * Enables testing without Android Context.
 */
interface SettingsProvider {
    val temperatureUnit: Flow<String>
    val windSpeedUnit: Flow<String>
    val language: Flow<String>
    val alertsEnabled: Flow<Boolean>
    val alertCondition: Flow<String>
}
