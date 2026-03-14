package com.example.weatherapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.preferences.SettingsDataStore
import com.example.weatherapp.utils.cancelWeatherAlerts
import com.example.weatherapp.utils.scheduleWeatherAlerts
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val dataStore: SettingsDataStore
) : ViewModel() {

    val alertsEnabled =
        dataStore.alertsEnabled.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            false
        )

    val alertCondition =
        dataStore.alertCondition.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            "Rain"
        )

    val temperatureUnit =
        dataStore.temperatureUnit.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            "metric"
        )

    val language =
        dataStore.language.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            "en"
        )

    fun toggleAlerts(
        context: Context,
        enabled: Boolean,
        lat: Double,
        lon: Double
    ) {

        viewModelScope.launch {

            dataStore.setAlertsEnabled(enabled)

            if (enabled) {
                scheduleWeatherAlerts(context, lat, lon)
            } else {
                cancelWeatherAlerts(context)
            }
        }
    }

    fun setAlertCondition(condition: String) {

        viewModelScope.launch {
            dataStore.setAlertCondition(condition)
        }
    }

    fun setTemperatureUnit(unit: String) {

        viewModelScope.launch {
            dataStore.setTemperatureUnit(unit)
        }
    }

    fun setLanguage(lang: String) {

        viewModelScope.launch {
            dataStore.setLanguage(lang)
        }
    }
}


class SettingsViewModelFactory(
    private val dataStore: SettingsDataStore
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(dataStore) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}