package com.example.weatherapp.ui.alerts

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.data.local.alert.WeatherAlert
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AlertsScreen(
    alerts: List<WeatherAlert>,
    onSaveAlert: (
        condition: String,
        startTime: Long,
        endTime: Long
    ) -> Unit,
    onDeleteAlert: (WeatherAlert) -> Unit
) {
    val context = LocalContext.current

    var selectedCondition by remember { mutableStateOf("Rain") }

    val calendar = remember { Calendar.getInstance() }
    var startTime by remember { mutableStateOf(System.currentTimeMillis() + 60_000L) }
    var endTime by remember { mutableStateOf(System.currentTimeMillis() + 3_600_000L) }

    val formatter = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

    val conditions = listOf("Rain", "Snow", "Wind", "Clear", "Clouds", "Thunderstorm")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ---- CREATE ALARM SECTION ----
        item {
            Text(
                text = "Create Weather Alarm",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Weather Condition",
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Condition chips in a flow-like layout
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                conditions.take(3).forEach { condition ->
                    FilterChip(
                        selected = selectedCondition == condition,
                        onClick = { selectedCondition = condition },
                        label = { Text(condition) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                conditions.drop(3).forEach { condition ->
                    FilterChip(
                        selected = selectedCondition == condition,
                        onClick = { selectedCondition = condition },
                        label = { Text(condition) }
                    )
                }
            }
        }

        // Time pickers
        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Start Time
            Text(
                text = "Start Time",
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedButton(
                onClick = {
                    val cal = Calendar.getInstance().apply { timeInMillis = startTime }
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    val selected = Calendar.getInstance().apply {
                                        set(year, month, day, hour, minute, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    startTime = selected.timeInMillis
                                },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                true
                            ).show()
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(formatter.format(Date(startTime)))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // End Time
            Text(
                text = "End Time",
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedButton(
                onClick = {
                    val cal = Calendar.getInstance().apply { timeInMillis = endTime }
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    val selected = Calendar.getInstance().apply {
                                        set(year, month, day, hour, minute, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    endTime = selected.timeInMillis
                                },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                true
                            ).show()
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(formatter.format(Date(endTime)))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Create button
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onSaveAlert(selectedCondition, startTime, endTime)
                }
            ) {
                Text("Create Alarm")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick test button
            OutlinedButton(
                onClick = {
                    val trigger = System.currentTimeMillis() + 5000
                    onSaveAlert(selectedCondition, trigger, trigger + 60000)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Test Alarm (5s)")
            }
        }

        // ---- SAVED ALARMS SECTION ----
        item {
            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Saved Alarms",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (alerts.isEmpty()) {
            item {
                Text(
                    text = "No alarms set",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(alerts, key = { it.id }) { alert ->
            AlertItem(
                alert = alert,
                formatter = formatter,
                onDelete = { onDeleteAlert(alert) }
            )
        }

        // Bottom padding
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AlertItem(
    alert: WeatherAlert,
    formatter: SimpleDateFormat,
    onDelete: () -> Unit
) {
    val conditionEmoji = when (alert.condition.lowercase()) {
        "rain" -> "🌧️"
        "snow" -> "❄️"
        "wind" -> "💨"
        "clear" -> "☀️"
        "clouds" -> "☁️"
        "thunderstorm" -> "⛈️"
        else -> "⚠️"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Condition info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$conditionEmoji ${alert.condition}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "From: ${formatter.format(Date(alert.startTime))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "To: ${formatter.format(Date(alert.endTime))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete alarm",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}