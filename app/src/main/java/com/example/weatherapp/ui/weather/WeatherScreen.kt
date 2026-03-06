package com.example.weatherapp.ui.weather

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(
    lat: Double,
    lon: Double,
    viewModel: WeatherViewModel
) {

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(lat, lon) {
        viewModel.loadWeather(lat, lon)
    }

    when (state) {

        is WeatherUiState.Loading -> {
            CircularProgressIndicator()
        }

        is WeatherUiState.Error -> {
            Text((state as WeatherUiState.Error).message)
        }

        is WeatherUiState.Success -> {

            val weather =
                (state as WeatherUiState.Success).weather

            Column {

                Text(weather.name)

                Text("${weather.main.temp}°C")

                Text(weather.weather[0].description)

                Text("Humidity: ${weather.main.humidity}%")

                Text("Wind: ${weather.wind.speed} m/s")
            }
        }
    }
}