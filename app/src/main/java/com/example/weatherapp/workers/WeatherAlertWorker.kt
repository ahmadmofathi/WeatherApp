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
import com.example.weatherapp.data.remote.RetrofitInstance

class WeatherAlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        return try {

            val lat = inputData.getDouble("lat", 0.0)
            val lon = inputData.getDouble("lon", 0.0)

            val response =
                RetrofitInstance.api.getHourlyForecast(
                    lat = lat,
                    lon = lon,
                    units = "metric"
                )

            val nextHour =
                response.hourly.firstOrNull()

            val condition =
                nextHour?.weather?.firstOrNull()?.main ?: ""
            //Test
            sendNotification("Test Notification please take care")

            if (condition == "Rain") {

                sendNotification(
                    "Rain expected in the next hour"
                )
            }

            Result.success()

        } catch (e: Exception) {

            Result.retry()
        }
    }

    private fun sendNotification(message: String) {

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
                .setContentTitle("Weather Alert")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        NotificationManagerCompat
            .from(applicationContext)
            .notify(1001, builder.build())
    }
}