package com.example.weatherapp.ui.weather.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weatherapp.data.remote.dto.Daily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
@Composable
fun DailyItem(
    day: String,
    iconUrl: String,
    max: Int,
    min: Int
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(day)

        AsyncImage(
            model = iconUrl,
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )

        Row {

            Text("$max°")

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "$min°",
                color = Color.Gray
            )
        }
    }
}