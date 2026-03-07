package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.remote.dto.Daily
import com.example.weatherapp.data.remote.dto.Hourly
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

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _hourlyForecast =
        MutableStateFlow<List<Hourly>>(emptyList())
    val hourlyForecast: StateFlow<List<Hourly>> = _hourlyForecast

    private val _dailyForecast =
        MutableStateFlow<List<Daily>>(emptyList())
    val dailyForecast: StateFlow<List<Daily>> = _dailyForecast

    private var lastLat: Double = 0.0
    private var lastLon: Double = 0.0

    fun loadWeather(lat: Double, lon: Double) {

        lastLat = lat
        lastLon = lon

        viewModelScope.launch {

            _isRefreshing.value = true

            try {

                val response =
                    repository.getWeather(lat, lon)

                _uiState.value =
                    WeatherUiState.Success(response)

                val hourly =
                    repository.getHourlyForecast(lat, lon)

                _hourlyForecast.value =
                    hourly.hourly.take(12)

                val daily =
                    repository.getDailyForecast(lat, lon)

                _dailyForecast.value =
                    daily.daily.take(5)

            } catch (e: Exception) {

                _uiState.value =
                    WeatherUiState.Error(
                        e.message ?: "Network error"
                    )
            }

            _isRefreshing.value = false
        }
    }

    fun reloadWeather() {
        loadWeather(lastLat, lastLon)
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