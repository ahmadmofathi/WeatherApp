package com.example.weatherapp.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R

@Composable
fun SettingsScreen(

    alertsEnabled: Boolean,
    alertCondition: String,
    temperatureUnit: String,
    language: String,

    onAlertsChanged: (Boolean) -> Unit,
    onConditionChanged: (String) -> Unit,
    onUnitChanged: (String) -> Unit,
    onLanguageChanged: (String) -> Unit

) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Alerts Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(stringResource(R.string.weather_alerts))

            Switch(
                checked = alertsEnabled,
                onCheckedChange = onAlertsChanged
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Weather Condition Selector
        if (alertsEnabled) {

            Text(stringResource(R.string.alert_condition))

            Spacer(modifier = Modifier.height(10.dp))

            Row {

                FilterChip(
                    selected = alertCondition == "Rain",
                    onClick = { onConditionChanged("Rain") },
                    label = { Text(stringResource(R.string.rain)) }
                )

                Spacer(modifier = Modifier.width(8.dp))

                FilterChip(
                    selected = alertCondition == "Snow",
                    onClick = { onConditionChanged("Snow") },
                    label = { Text(stringResource(R.string.snow)) }
                )

                Spacer(modifier = Modifier.width(8.dp))

                FilterChip(
                    selected = alertCondition == "Clear",
                    onClick = { onConditionChanged("Clear") },
                    label = { Text(stringResource(R.string.clear)) }
                )

                Spacer(modifier = Modifier.width(8.dp))

                FilterChip(
                    selected = alertCondition == "Wind",
                    onClick = { onConditionChanged("Wind") },
                    label = { Text(stringResource(R.string.wind)) }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Temperature Unit
        Text(stringResource(R.string.temperature_unit))

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
        Text(stringResource(R.string.language))

        Row {

            FilterChip(
                selected = language == "en",
                onClick = { onLanguageChanged("en") },
                label = { Text(stringResource(R.string.english)) }
            )

            Spacer(modifier = Modifier.width(10.dp))

            FilterChip(
                selected = language == "ar",
                onClick = { onLanguageChanged("ar") },
                label = { Text(stringResource(R.string.arabic)) }
            )
        }
    }
}