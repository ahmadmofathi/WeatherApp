package com.example.weatherapp.ui.weather.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.R

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
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.25f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            WeatherStatItem(
                title = stringResource(R.string.humidity),
                value = "$humidity%"
            )

            WeatherStatItem(
                title = stringResource(R.string.wind),
                value = "${wind.toInt()} ${stringResource(R.string.kmh)}"
            )

            WeatherStatItem(
                title = stringResource(R.string.pressure),
                value = "$pressure ${stringResource(R.string.hpa)}"
            )

            WeatherStatItem(
                title = stringResource(R.string.clouds),
                value = "$clouds%"
            )
        }
    }
}