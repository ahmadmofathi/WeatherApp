package com.example.weatherapp.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ─── Notifications Card ─────────────────────────────────

        SettingsSectionCard(
            icon = Icons.Default.Notifications,
            title = stringResource(R.string.notifications)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.weather_alerts),
                    style = MaterialTheme.typography.bodyLarge
                )

                Switch(
                    checked = alertsEnabled,
                    onCheckedChange = onAlertsChanged
                )
            }

            // Condition chips (only when alerts enabled)
            if (alertsEnabled) {

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.alert_condition),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    FilterChip(
                        selected = alertCondition == "Rain",
                        onClick = { onConditionChanged("Rain") },
                        label = { Text(stringResource(R.string.rain)) },
                        shape = RoundedCornerShape(12.dp)
                    )

                    FilterChip(
                        selected = alertCondition == "Snow",
                        onClick = { onConditionChanged("Snow") },
                        label = { Text(stringResource(R.string.snow)) },
                        shape = RoundedCornerShape(12.dp)
                    )

                    FilterChip(
                        selected = alertCondition == "Clear",
                        onClick = { onConditionChanged("Clear") },
                        label = { Text(stringResource(R.string.clear)) },
                        shape = RoundedCornerShape(12.dp)
                    )

                    FilterChip(
                        selected = alertCondition == "Wind",
                        onClick = { onConditionChanged("Wind") },
                        label = { Text(stringResource(R.string.wind)) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ─── Temperature Unit Card ──────────────────────────────

        SettingsSectionCard(
            icon = Icons.Default.Thermostat,
            title = stringResource(R.string.temperature_unit)
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                FilterChip(
                    selected = temperatureUnit == "metric",
                    onClick = { onUnitChanged("metric") },
                    label = { Text("°C") },
                    shape = RoundedCornerShape(12.dp)
                )

                FilterChip(
                    selected = temperatureUnit == "imperial",
                    onClick = { onUnitChanged("imperial") },
                    label = { Text("°F") },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ─── Language Card ──────────────────────────────────────

        SettingsSectionCard(
            icon = Icons.Default.Language,
            title = stringResource(R.string.language)
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                FilterChip(
                    selected = language == "en",
                    onClick = { onLanguageChanged("en") },
                    label = { Text(stringResource(R.string.english)) },
                    shape = RoundedCornerShape(12.dp)
                )

                FilterChip(
                    selected = language == "ar",
                    onClick = { onLanguageChanged("ar") },
                    label = { Text(stringResource(R.string.arabic)) },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionCard(
    icon: ImageVector,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}