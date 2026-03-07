package com.example.weatherapp.ui.weather.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun WeatherHero(
    temperature: Int,
    description: String,
    high: Int,
    low: Int,
    iconUrl: String
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {

        WeatherAnimation(
            weatherType = iconUrl
        )

        Text(
            text = "$temperature°",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = description.uppercase(),
            style = MaterialTheme.typography.titleMedium,
            letterSpacing = 4.sp
        )

        Text(
            text = "H:$high°   L:$low°",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}