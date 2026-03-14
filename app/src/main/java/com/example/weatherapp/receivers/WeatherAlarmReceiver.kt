package com.example.weatherapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.room.Room
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.remote.RetrofitInstance
import com.example.weatherapp.services.AlarmService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alertId = intent.getIntExtra("alert_id", -1)
        Log.d("WeatherAlarmReceiver", "Received alarm for alertId=$alertId")

        if (alertId == -1) {
            // Legacy or test alarm — just fire
            startAlarmService(context, alertId, "Weather Alert")
            return
        }

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_db"
                ).fallbackToDestructiveMigration().build()

                val alert = db.alertDao().getAlertById(alertId)

                if (alert == null) {
                    Log.w("WeatherAlarmReceiver", "Alert id=$alertId not found in database")
                    pendingResult.finish()
                    return@launch
                }

                // Check if alarm is still within its valid time range
                val now = System.currentTimeMillis()
                if (now > alert.endTime) {
                    Log.d("WeatherAlarmReceiver", "Alert id=$alertId has expired (endTime=${alert.endTime})")
                    pendingResult.finish()
                    return@launch
                }

                // Call weather API to check current condition
                val response = RetrofitInstance.api.getForecast(
                    lat = alert.latitude,
                    lon = alert.longitude,
                    units = "metric",
                    lang = "en"
                )

                val currentCondition = response.weather.firstOrNull()?.main ?: ""
                Log.d("WeatherAlarmReceiver", "Current condition: $currentCondition, Alert condition: ${alert.condition}")

                if (currentCondition.equals(alert.condition, ignoreCase = true)) {
                    Log.d("WeatherAlarmReceiver", "Condition MATCHES — launching alarm!")
                    startAlarmService(context, alertId, alert.condition)
                } else {
                    Log.d("WeatherAlarmReceiver", "Condition does NOT match — skipping alarm")
                }

            } catch (e: Exception) {
                Log.e("WeatherAlarmReceiver", "Error checking weather condition", e)
                // On network error, still fire the alarm as fallback
                startAlarmService(context, alertId, "Weather Alert")
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun startAlarmService(context: Context, alertId: Int, condition: String) {
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("alert_id", alertId)
            putExtra("condition", condition)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
