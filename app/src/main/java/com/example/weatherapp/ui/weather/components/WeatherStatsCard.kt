package com.example.weatherapp.ui.weather.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WeatherStatsCard(
    humidity: Int,
    wind: Double,
    uv: Int
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            WeatherStatItem(
                title = "HUMIDITY",
                value = "$humidity%"
            )

            WeatherStatItem(
                title = "WIND",
                value = "${wind} km/h"
            )

            WeatherStatItem(
                title = "UV INDEX",
                value = "$uv"
            )
        }
    }
}