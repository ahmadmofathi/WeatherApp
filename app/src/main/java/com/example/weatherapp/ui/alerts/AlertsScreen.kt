package com.example.weatherapp.ui.alerts

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
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

    var startTime by remember { mutableStateOf(System.currentTimeMillis() + 60_000L) }
    var endTime by remember { mutableStateOf(System.currentTimeMillis() + 3_600_000L) }

    val formatter = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

    // Conditions with their string resource IDs
    data class ConditionItem(val key: String, val labelResId: Int, val emoji: String)

    val conditions = listOf(
        ConditionItem("Rain", R.string.rain, "🌧️"),
        ConditionItem("Snow", R.string.snow, "❄️"),
        ConditionItem("Wind", R.string.wind, "💨"),
        ConditionItem("Clear", R.string.clear, "☀️"),
        ConditionItem("Clouds", R.string.clouds, "☁️"),
        ConditionItem("Thunderstorm", R.string.thunderstorm, "⛈️")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Create Alarm Section ──
        item {
            Text(
                text = stringResource(R.string.create_weather_alarm),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.weather_condition),
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Condition chips — row 1
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                conditions.take(3).forEach { condition ->
                    FilterChip(
                        selected = selectedCondition == condition.key,
                        onClick = { selectedCondition = condition.key },
                        label = {
                            Text("${condition.emoji} ${stringResource(condition.labelResId)}")
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Condition chips — row 2
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                conditions.drop(3).forEach { condition ->
                    FilterChip(
                        selected = selectedCondition == condition.key,
                        onClick = { selectedCondition = condition.key },
                        label = {
                            Text("${condition.emoji} ${stringResource(condition.labelResId)}")
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Time pickers
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.start_time),
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(formatter.format(Date(startTime)))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.end_time),
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(formatter.format(Date(endTime)))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Create button
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                onClick = {
                    onSaveAlert(selectedCondition, startTime, endTime)
                }
            ) {
                Text(
                    stringResource(R.string.create_alarm),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Test button
            OutlinedButton(
                onClick = {
                    val trigger = System.currentTimeMillis() + 5000
                    onSaveAlert(selectedCondition, trigger, trigger + 60000)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(stringResource(R.string.test_alarm))
            }
        }

        // ── Saved Alarms Section ──
        item {
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.saved_alarms),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (alerts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsNone,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = stringResource(R.string.no_alarms_set),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = stringResource(R.string.no_alarms_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        items(alerts, key = { it.id }) { alert ->
            AlertItem(
                alert = alert,
                formatter = formatter,
                onDelete = { onDeleteAlert(alert) }
            )
        }

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
    val conditionEmojis = mapOf(
        "Rain" to "🌧️", "Snow" to "❄️", "Wind" to "💨",
        "Clear" to "☀️", "Clouds" to "☁️", "Thunderstorm" to "⛈️"
    )
    val emoji = conditionEmojis[alert.condition] ?: "⚠️"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$emoji ${alert.condition}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${stringResource(R.string.start_time)}: ${formatter.format(Date(alert.startTime))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "${stringResource(R.string.end_time)}: ${formatter.format(Date(alert.endTime))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}