package com.example.weatherapp.workers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherapp.R
import com.example.weatherapp.data.preferences.SettingsDataStore
import com.example.weatherapp.data.remote.RetrofitInstance
import com.example.weatherapp.model.WeatherCondition
import kotlinx.coroutines.flow.first

class WeatherAlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        return try {

            val lat = inputData.getDouble("lat", 0.0)
            val lon = inputData.getDouble("lon", 0.0)

            val dataStore = SettingsDataStore(applicationContext)

            val alertsEnabled = dataStore.alertsEnabled.first()
            val selectedCondition = dataStore.alertCondition.first()
            val unit = dataStore.temperatureUnit.first()

            if (!alertsEnabled) {
                return Result.success()
            }

            // Map kelvin to standard for API
            val apiUnit = if (unit == "kelvin") "standard" else unit

            val response =
                RetrofitInstance.api.getHourlyForecast(
                    lat = lat,
                    lon = lon,
                    units = apiUnit
                )

            val nextHour =
                response.hourly.firstOrNull()

            val condition =
                nextHour?.weather?.firstOrNull()?.main ?: ""

            if (condition.equals(selectedCondition, ignoreCase = true)) {

                // Use localized condition name via WeatherCondition enum
                val weatherCondition = WeatherCondition.fromApiName(condition)
                val conditionLabel = weatherCondition?.let {
                    applicationContext.getString(it.labelResId)
                } ?: condition

                val message = applicationContext.getString(
                    R.string.condition_expected,
                    conditionLabel
                )

                val title = applicationContext.getString(
                    R.string.weather_alert_notification_title
                )

                sendNotification(title, message)
            }

            Result.success()

        } catch (e: Exception) {

            Result.retry()
        }
    }

    private fun sendNotification(title: String, message: String) {

        if (
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val builder =
            NotificationCompat.Builder(
                applicationContext,
                "weather_alerts"
            )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        NotificationManagerCompat
            .from(applicationContext)
            .notify(1001, builder.build())
    }
}