package com.example.weatherapp.ui.weather

import com.example.weatherapp.data.remote.dto.ForecastResponse

sealed class WeatherUiState {

    object Loading : WeatherUiState()

    data class Success(
        val weather: ForecastResponse
    ) : WeatherUiState()

    data class Error(
        val message: String
    ) : WeatherUiState()

}