package com.example.weatherapp.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.weatherapp.receivers.WeatherAlarmReceiver

fun scheduleAlarm(
    context: Context,
    triggerTime: Long,
    alertId: Int
) {
    Log.d("AlarmScheduler", "Scheduling alarm id=$alertId for $triggerTime")

    val intent = Intent(context, WeatherAlarmReceiver::class.java).apply {
        putExtra("alert_id", alertId)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        alertId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w("AlarmScheduler", "Cannot schedule exact alarms — permission not granted")
                return
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    } catch (e: SecurityException) {
        Log.e("AlarmScheduler", "SecurityException scheduling alarm", e)
    }
}

fun cancelAlarm(
    context: Context,
    alertId: Int
) {
    Log.d("AlarmScheduler", "Cancelling alarm id=$alertId")

    val intent = Intent(context, WeatherAlarmReceiver::class.java)

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        alertId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    alarmManager.cancel(pendingIntent)
}

fun scheduleSnooze(
    context: Context,
    alertId: Int
) {
    val snoozeTime = System.currentTimeMillis() + 10 * 60 * 1000L // 10 minutes
    Log.d("AlarmScheduler", "Scheduling snooze for alarm id=$alertId at $snoozeTime")
    scheduleAlarm(context, snoozeTime, alertId)
}