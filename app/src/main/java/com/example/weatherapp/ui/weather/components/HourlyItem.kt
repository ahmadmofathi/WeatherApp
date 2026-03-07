package com.example.weatherapp.ui.weather.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weatherapp.data.remote.dto.Hourly
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
@Composable
fun HourlyItem(
    time: String,
    iconUrl: String,
    temp: Int,
    isNow: Boolean = false
) {

    Card(
        colors = if (isNow)
            CardDefaults.cardColors(
                containerColor = Color(0xFFFF8C00)
            )
        else
            CardDefaults.cardColors(),
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier
            .width(80.dp)
            .padding(6.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {

            Text(time, maxLines = 1)

            AsyncImage(
                model = iconUrl,
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )

            Text("$temp°")
        }
    }
}