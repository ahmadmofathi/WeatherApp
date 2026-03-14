package com.example.weatherapp.ui.weather.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Compress
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.WeatherColors

@Composable
fun WeatherStatsCard(
    humidity: Int,
    wind: Double,
    pressure: Int,
    clouds: Int,
    windSpeedUnit: String = "mps",
    temperatureUnit: String = "metric"
) {

    // Determine wind display unit
    val windValue: Double
    val windUnitLabel: String

    when (windSpeedUnit) {
        "mph" -> {
            // API returns m/s for metric/kelvin, mph for imperial
            // If temperatureUnit is imperial, API already returns mph
            windValue = if (temperatureUnit == "imperial") wind else wind * 2.237
            windUnitLabel = stringResource(R.string.unit_mph)
        }
        else -> {
            // mps
            windValue = if (temperatureUnit == "imperial") wind / 2.237 else wind
            windUnitLabel = stringResource(R.string.unit_mps)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = WeatherColors.GlassWhiteStrong
        ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, WeatherColors.GlassBorder)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            WeatherStatItem(
                icon = Icons.Outlined.WaterDrop,
                title = stringResource(R.string.humidity),
                value = "$humidity%"
            )

            WeatherStatItem(
                icon = Icons.Outlined.Air,
                title = stringResource(R.string.wind),
                value = "${windValue.toInt()} $windUnitLabel"
            )

            WeatherStatItem(
                icon = Icons.Outlined.Compress,
                title = stringResource(R.string.pressure),
                value = "$pressure ${stringResource(R.string.hpa)}"
            )

            WeatherStatItem(
                icon = Icons.Outlined.Cloud,
                title = stringResource(R.string.clouds),
                value = "$clouds%"
            )
        }
    }
}