package com.example.weatherapp.ui.weather.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weatherapp.ui.theme.WeatherColors

@Composable
fun HourlyItem(
    time: String,
    iconUrl: String,
    temp: Int,
    isNow: Boolean = false
) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isNow)
                Color.White.copy(alpha = 0.35f)
            else
                WeatherColors.GlassWhite
        ),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(
            1.dp,
            if (isNow)
                Color.White.copy(alpha = 0.5f)
            else
                WeatherColors.GlassBorder
        ),
        modifier = Modifier.width(80.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 16.dp
            )
        ) {

            Text(
                text = time,
                maxLines = 1,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.White.copy(alpha = 0.7f)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            AsyncImage(
                model = iconUrl,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$temp°",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}