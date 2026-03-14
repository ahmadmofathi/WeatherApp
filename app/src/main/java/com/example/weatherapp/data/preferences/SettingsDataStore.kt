package com.example.weatherapp.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class SettingsDataStore(private val context: Context) : SettingsProvider {

    companion object {
        val ALERTS = booleanPreferencesKey("alerts_enabled")
        val UNIT = stringPreferencesKey("temperature_unit")
        val LANGUAGE = stringPreferencesKey("language")
        val ALERT_CONDITION = stringPreferencesKey("alert_condition")
    }

    override val alertsEnabled =
        context.dataStore.data.map { prefs ->
            prefs[ALERTS] ?: false
        }

    override val temperatureUnit =
        context.dataStore.data.map { prefs ->
            prefs[UNIT] ?: "metric"
        }

    override val language =
        context.dataStore.data.map { prefs ->
            prefs[LANGUAGE] ?: "en"
        }

    override val alertCondition =
        context.dataStore.data.map { prefs ->
            prefs[ALERT_CONDITION] ?: "Rain"
        }

    suspend fun setAlertsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ALERTS] = enabled
        }
    }

    suspend fun setTemperatureUnit(unit: String) {
        context.dataStore.edit { prefs ->
            prefs[UNIT] = unit
        }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE] = lang
        }
    }

    suspend fun setAlertCondition(condition: String) {
        context.dataStore.edit { prefs ->
            prefs[ALERT_CONDITION] = condition
        }
    }
}