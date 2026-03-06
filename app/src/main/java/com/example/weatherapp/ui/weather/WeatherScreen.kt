package com.example.weatherapp.ui.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weatherapp.ui.weather.components.*
import com.example.weatherapp.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeatherScreen(
    lat: Double,
    lon: Double,
    viewModel: WeatherViewModel
) {

    val state by viewModel.uiState.collectAsState()
    val hourly by viewModel.hourlyForecast.collectAsState()
    val daily by viewModel.dailyForecast.collectAsState()

    LaunchedEffect(lat, lon) {
        viewModel.loadWeather(lat, lon)
    }

    when (state) {

        is WeatherUiState.Loading -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is WeatherUiState.Error -> {

            val message =
                (state as WeatherUiState.Error).message

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(message)
            }
        }

        is WeatherUiState.Success -> {

            val weather =
                (state as WeatherUiState.Success).weather

            val iconCode =
                weather.weather[0].icon

            val iconUrl =
                "https://openweathermap.org/img/wn/${iconCode}@2x.png"

            val weatherType =
                weather.weather[0].main

            val background = when (weatherType) {

                "Clear" -> Brush.verticalGradient(
                    listOf(Color(0xFF4FC3F7), Color(0xFFFFE082))
                )

                "Clouds" -> Brush.verticalGradient(
                    listOf(Color(0xFF90A4AE), Color(0xFFECEFF1))
                )

                "Rain" -> Brush.verticalGradient(
                    listOf(Color(0xFF455A64), Color(0xFF90CAF9))
                )

                else -> Brush.verticalGradient(
                    listOf(Color(0xFF4FC3F7), Color(0xFFE3F2FD))
                )
            }

            LazyColumn(

                modifier = Modifier
                    .fillMaxSize()
                    .background(background),

                contentPadding = PaddingValues(16.dp)

            ) {

                item {

                    WeatherHeader(
                        city = weather.name
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    WeatherHero(
                        temperature = weather.main.temp.toInt(),
                        description = weather.weather[0].description,
                        high = weather.main.temp_max.toInt(),
                        low = weather.main.temp_min.toInt(),
                        iconUrl = iconUrl
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    WeatherStatsCard(
                        humidity = weather.main.humidity,
                        wind = weather.wind.speed * 3.6,
                        uv = 2
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {

                    Text(
                        text = "NEXT 24 HOURS",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow {

                        items(hourly) { hour ->

                            val icon =
                                hour.weather[0].icon

                            val iconUrl =
                                "https://openweathermap.org/img/wn/${icon}@2x.png"

                            HourlyItem(
                                time = formatHour(hour.dt),
                                iconUrl = iconUrl,
                                temp = hour.temp.toInt(),
                                isNow = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {

                    Text(
                        text = "5-DAY FORECAST",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(daily) { day ->

                    val icon =
                        day.weather[0].icon

                    val iconUrl =
                        "https://openweathermap.org/img/wn/${icon}@2x.png"

                    DailyItem(
                        day = formatDay(day.dt),
                        iconUrl = iconUrl,
                        max = day.temp.max.toInt(),
                        min = day.temp.min.toInt()
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

fun formatHour(timestamp: Long): String {

    val date = Date(timestamp * 1000)

    val formatter =
        SimpleDateFormat("HH:mm", Locale.getDefault())

    return formatter.format(date)
}

fun formatDay(timestamp: Long): String {

    val date = Date(timestamp * 1000)

    val formatter =
        SimpleDateFormat("EEEE", Locale.getDefault())

    return formatter.format(date)
}