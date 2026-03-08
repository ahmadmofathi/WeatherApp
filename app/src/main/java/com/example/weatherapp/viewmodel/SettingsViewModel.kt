package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.preferences.SettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val dataStore: SettingsDataStore
) : ViewModel() {

    val alertsEnabled =
        dataStore.alertsEnabled.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            false
        )

    val temperatureUnit =
        dataStore.temperatureUnit.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            "metric"
        )

    val language =
        dataStore.language.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            "en"
        )

    fun setAlertsEnabled(enabled: Boolean) {

        viewModelScope.launch {
            dataStore.setAlertsEnabled(enabled)
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