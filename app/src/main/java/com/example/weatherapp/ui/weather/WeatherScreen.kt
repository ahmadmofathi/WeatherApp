package com.example.weatherapp.ui.weather

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
import com.example.weatherapp.ui.theme.WeatherColors
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
    isFavorite: Boolean,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onToggleFavoriteClick: () -> Unit
) {

    val state by viewModel.uiState.collectAsState()
    val hourly by viewModel.hourlyForecast.collectAsState()
    val daily by viewModel.dailyForecast.collectAsState()

    val refreshing by viewModel.isRefreshing.collectAsState()
    val location by viewModel.location.collectAsState()
    val tempUnit by viewModel.temperatureUnit.collectAsState()
    val windUnit by viewModel.windSpeedUnit.collectAsState()

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
            ShimmerLoadingScreen()
        }

        is WeatherUiState.Error -> {

            val message =
                (state as WeatherUiState.Error).message

            ErrorScreen(
                message = message,
                onRetry = { viewModel.reloadWeather() }
            )
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
                isFavorite = isFavorite,
                onMenuClick = onMenuClick,
                onSearchClick = onSearchClick,
                onToggleFavoriteClick = onToggleFavoriteClick,
                isOffline = false,
                temperatureUnit = tempUnit,
                windSpeedUnit = windUnit
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
                isFavorite = isFavorite,
                onMenuClick = onMenuClick,
                onSearchClick = onSearchClick,
                onToggleFavoriteClick = onToggleFavoriteClick,
                isOffline = true,
                temperatureUnit = tempUnit,
                windSpeedUnit = windUnit
            )
        }
    }
}

// ── Shimmer Loading Skeleton ─────────────────────────────────────────

@Composable
fun ShimmerLoadingScreen() {
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.1f),
        Color.White.copy(alpha = 0.25f),
        Color.White.copy(alpha = 0.1f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim.value - 200f, 0f),
        end = Offset(translateAnim.value, 0f)
    )

    val background = Brush.verticalGradient(WeatherColors.Default)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(80.dp))
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(96.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(110.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(brush)
                    )
                }
            }
        }
    }
}

// ── Error Screen ─────────────────────────────────────────────────────

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A237E), Color(0xFF0D47A1))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "📡",
                style = MaterialTheme.typography.displayMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "No Internet Connection",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF1A237E)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Retry",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

// ── Main Weather Content ─────────────────────────────────────────────

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun WeatherContent(
    weather: ForecastResponse,
    hourly: List<Hourly>,
    daily: List<Daily>,
    refreshing: Boolean,
    pullRefreshState: androidx.compose.material.pullrefresh.PullRefreshState,
    isFavorite: Boolean,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onToggleFavoriteClick: () -> Unit,
    isOffline: Boolean,
    temperatureUnit: String = "metric",
    windSpeedUnit: String = "mps"
) {
    val weatherType = weather.weather[0].main
    val date = getCurrentDate()
    val time = getCurrentTime()

    val background = when (weatherType) {
        "Clear" -> Brush.verticalGradient(WeatherColors.ClearDay)
        "Clouds" -> Brush.verticalGradient(WeatherColors.Clouds)
        "Rain", "Drizzle" -> Brush.verticalGradient(WeatherColors.Rain)
        "Thunderstorm" -> Brush.verticalGradient(WeatherColors.Thunderstorm)
        "Snow" -> Brush.verticalGradient(WeatherColors.Snow)
        else -> Brush.verticalGradient(WeatherColors.Default)
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
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF3E0).copy(alpha = 0.9f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📴",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.width(10.dp))

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
                    isFavorite = isFavorite,
                    onMenuClick = onMenuClick,
                    onSearchClick = onSearchClick,
                    onToggleFavoriteClick = onToggleFavoriteClick
                )

                Spacer(modifier = Modifier.height(8.dp))

                WeatherHero(
                    temperature = weather.main.temp.toInt(),
                    description = weather.weather[0].description,
                    high = weather.main.temp_max.toInt(),
                    low = weather.main.temp_min.toInt(),
                    iconUrl = weather.weather[0].main,
                    temperatureUnit = temperatureUnit
                )

                Spacer(modifier = Modifier.height(24.dp))

                WeatherStatsCard(
                    humidity = weather.main.humidity,
                    wind = weather.wind.speed,
                    pressure = weather.main.pressure,
                    clouds = weather.clouds.all,
                    windSpeedUnit = windSpeedUnit,
                    temperatureUnit = temperatureUnit
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Hourly forecast section
            item {
                Text(
                    text = stringResource(R.string.next_24_hours),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    items(hourly) { hour ->

                        val icon = hour.weather[0].icon

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

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Daily forecast section
            item {
                Text(
                    text = stringResource(R.string.five_day_forecast),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            items(daily, key = { it.dt }) { day ->

                val icon = day.weather[0].icon

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
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = Color.White,
            contentColor = Color(0xFF1565C0)
        )
    }
}

fun formatHour(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(date)
}

fun formatDay(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val formatter = SimpleDateFormat("EEEE", Locale.getDefault())
    return formatter.format(date)
}