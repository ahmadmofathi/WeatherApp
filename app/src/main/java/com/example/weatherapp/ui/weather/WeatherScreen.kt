package com.example.weatherapp.ui.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.weatherapp.data.remote.dto.ForecastResponse
import com.example.weatherapp.data.remote.dto.Hourly
import com.example.weatherapp.data.remote.dto.Daily
import com.example.weatherapp.ui.weather.components.*
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.example.weatherapp.utils.getCurrentDate
import com.example.weatherapp.utils.getCurrentTime
import java.text.SimpleDateFormat
import com.example.weatherapp.R
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onAddFavoriteClick: () -> Unit
){

    val state by viewModel.uiState.collectAsState()
    val hourly by viewModel.hourlyForecast.collectAsState()
    val daily by viewModel.dailyForecast.collectAsState()

    val refreshing by viewModel.isRefreshing.collectAsState()
    val location by viewModel.location.collectAsState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = {
            viewModel.reloadWeather()
        }
    )

    LaunchedEffect(location) {

        location?.let { (lat, lon) ->
            viewModel.loadWeather(lat, lon)
        }
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "📡",
                        style = MaterialTheme.typography.displayLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No Internet Connection",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.reloadWeather() }
                    ) {
                        Text("Retry")
                    }
                }
            }
        }

        is WeatherUiState.Success -> {

            val weather =
                (state as WeatherUiState.Success).weather

            WeatherContent(
                weather = weather,
                hourly = hourly,
                daily = daily,
                refreshing = refreshing,
                pullRefreshState = pullRefreshState,
                onMenuClick = onMenuClick,
                onSearchClick = onSearchClick,
                onAddFavoriteClick = onAddFavoriteClick,
                isOffline = false
            )
        }

        is WeatherUiState.Offline -> {

            val weather =
                (state as WeatherUiState.Offline).weather

            WeatherContent(
                weather = weather,
                hourly = hourly,
                daily = daily,
                refreshing = refreshing,
                pullRefreshState = pullRefreshState,
                onMenuClick = onMenuClick,
                onSearchClick = onSearchClick,
                onAddFavoriteClick = onAddFavoriteClick,
                isOffline = true
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun WeatherContent(
    weather: ForecastResponse,
    hourly: List<Hourly>,
    daily: List<Daily>,
    refreshing: Boolean,
    pullRefreshState: androidx.compose.material.pullrefresh.PullRefreshState,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onAddFavoriteClick: () -> Unit,
    isOffline: Boolean
) {
    val weatherType =
        weather.weather[0].main

    val date = getCurrentDate()
    val time = getCurrentTime()

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .background(background),

            contentPadding = PaddingValues(16.dp)

        ) {

            // Offline banner
            if (isOffline) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF3E0)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📴",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Offline Mode — Showing last known data",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFE65100)
                                )
                            )
                        }
                    }
                }
            }

            item {

                WeatherHeader(
                    city = weather.name,
                    date = date,
                    time = time,
                    onMenuClick = onMenuClick,
                    onSearchClick = onSearchClick,
                    onAddFavoriteClick = onAddFavoriteClick
                )

                Spacer(modifier = Modifier.height(24.dp))

                WeatherHero(
                    temperature = weather.main.temp.toInt(),
                    description = weather.weather[0].description,
                    high = weather.main.temp_max.toInt(),
                    low = weather.main.temp_min.toInt(),
                    iconUrl = weather.weather[0].main
                )

                Spacer(modifier = Modifier.height(24.dp))

                WeatherStatsCard(
                    humidity = weather.main.humidity,
                    wind = weather.wind.speed * 3.6,
                    pressure = weather.main.pressure,
                    clouds = weather.clouds.all
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            item {

                Text(
                    text = stringResource(R.string.next_24_hours),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

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
                    text = stringResource(R.string.five_day_forecast),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            items(daily, key = { it.dt }) { day ->

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

        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
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