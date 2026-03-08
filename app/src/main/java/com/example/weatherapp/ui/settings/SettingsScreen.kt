package com.example.weatherapp.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(

    alertsEnabled: Boolean,
    temperatureUnit: String,
    language: String,

    onAlertsChanged: (Boolean) -> Unit,
    onUnitChanged: (String) -> Unit,
    onLanguageChanged: (String) -> Unit

) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Alerts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text("Weather Alerts")

            Switch(
                checked = alertsEnabled,
                onCheckedChange = onAlertsChanged
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Temperature Unit
        Text("Temperature Unit")

        Row {

            FilterChip(
                selected = temperatureUnit == "metric",
                onClick = { onUnitChanged("metric") },
                label = { Text("°C") }
            )

            Spacer(modifier = Modifier.width(10.dp))

            FilterChip(
                selected = temperatureUnit == "imperial",
                onClick = { onUnitChanged("imperial") },
                label = { Text("°F") }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Language
        Text("Language")

        Row {

            FilterChip(
                selected = language == "en",
                onClick = { onLanguageChanged("en") },
                label = { Text("English") }
            )

            Spacer(modifier = Modifier.width(10.dp))

            FilterChip(
                selected = language == "ar",
                onClick = { onLanguageChanged("ar") },
                label = { Text("Arabic") }
            )
        }
    }
}