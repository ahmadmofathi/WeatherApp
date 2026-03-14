package com.example.weatherapp.ui.weather.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeatherHero(
    temperature: Int,
    description: String,
    high: Int,
    low: Int,
    iconUrl: String,
    temperatureUnit: String = "metric"
) {

    val unitSuffix = when (temperatureUnit) {
        "imperial" -> "°F"
        "kelvin" -> " K"
        else -> "°C"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {

        WeatherAnimation(
            weatherType = iconUrl
        )

        // Giant temperature — thin weight, Apple Weather style
        Text(
            text = "$temperature$unitSuffix",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 112.sp,
                fontWeight = FontWeight.Thin,
                color = Color.White
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Weather description
        Text(
            text = description.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White.copy(alpha = 0.9f),
                letterSpacing = 2.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // High / Low
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "H:$high$unitSuffix",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = "L:$low$unitSuffix",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}