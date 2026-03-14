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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
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
    clouds: Int
) {

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
                value = "${wind.toInt()} ${stringResource(R.string.kmh)}"
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