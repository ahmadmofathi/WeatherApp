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
import com.example.weatherapp.model.WeatherCondition
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlertsScreen(
    alerts: List<WeatherAlert>,
    onSaveAlert: (
        condition: String,
        startTime: Long,
        endTime: Long
    ) -> Unit,
    onDeleteAlert: (WeatherAlert) -> Unit,
    onToggleAlert: (WeatherAlert, Boolean) -> Unit
) {
    val context = LocalContext.current

    var selectedCondition by remember { mutableStateOf("Rain") }

    var startTime by remember { mutableStateOf(System.currentTimeMillis() + 60_000L) }
    var endTime by remember { mutableStateOf(System.currentTimeMillis() + 3_600_000L) }

    val formatter = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

    // Delete confirmation dialog state
    var alertToDelete by remember { mutableStateOf<WeatherAlert?>(null) }

    // Delete confirmation dialog
    if (alertToDelete != null) {
        AlertDialog(
            onDismissRequest = { alertToDelete = null },
            title = {
                Text(
                    text = stringResource(R.string.delete_alarm_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.delete_alarm_message),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        alertToDelete?.let { onDeleteAlert(it) }
                        alertToDelete = null
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { alertToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Use WeatherCondition enum for selectable conditions
    val conditions = WeatherCondition.selectableConditions

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

        // Condition chips — wrapping layout
        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                conditions.forEach { condition ->
                    FilterChip(
                        selected = selectedCondition == condition.apiName,
                        onClick = { selectedCondition = condition.apiName },
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
                onDelete = { alertToDelete = alert },
                onToggle = { isActive -> onToggleAlert(alert, isActive) }
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
    onDelete: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    // Use WeatherCondition enum for emoji lookup
    val condition = WeatherCondition.fromApiName(alert.condition)
    val emoji = condition?.emoji ?: "⚠️"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (alert.isActive)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Use localized condition label
                val conditionLabel = condition?.let {
                    stringResource(it.labelResId)
                } ?: alert.condition

                Text(
                    text = "$emoji $conditionLabel",
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

            // Active / Inactive Switch
            Switch(
                checked = alert.isActive,
                onCheckedChange = onToggle
            )

            Spacer(modifier = Modifier.width(8.dp))

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
