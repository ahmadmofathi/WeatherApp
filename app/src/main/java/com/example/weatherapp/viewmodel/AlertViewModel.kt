package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.alert.WeatherAlert
import com.example.weatherapp.data.repository.AlertRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlertViewModel(
    private val repository: AlertRepository
) : ViewModel() {

    val alerts =
        repository.getAlerts().stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            emptyList()
        )

    fun addAlert(alert: WeatherAlert, onResult: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertAlert(alert)
            onResult(id)
        }
    }

    fun deleteAlert(alert: WeatherAlert) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
        }
    }
}

class AlertViewModelFactory(
    private val repository: AlertRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            return AlertViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel")
    }
}