package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.ui.weather.WeatherUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)

    val uiState: StateFlow<WeatherUiState> = _uiState

    fun loadWeather(lat: Double, lon: Double) {

        viewModelScope.launch {

            try {

                val response =
                    repository.getWeather(lat, lon)

                _uiState.value =
                    WeatherUiState.Success(response)

            } catch (e: Exception) {

                _uiState.value =
                    WeatherUiState.Error(
                        e.message ?: "Network error"
                    )
            }
        }
    }
}


class WeatherViewModelFactory(
    private val repository: WeatherRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}