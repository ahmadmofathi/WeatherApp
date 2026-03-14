package com.example.weatherapp.utils

import android.content.Context
import androidx.work.*
import com.example.weatherapp.workers.WeatherAlertWorker
import java.util.concurrent.TimeUnit

fun scheduleWeatherAlerts(
    context: Context,
    lat: Double,
    lon: Double
) {

    val data = workDataOf(
        "lat" to lat,
        "lon" to lon
    )

    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val request =
        PeriodicWorkRequestBuilder<WeatherAlertWorker>(
            1, TimeUnit.HOURS
        )
            .setInputData(data)
            .setConstraints(constraints)
            .build()

    WorkManager
        .getInstance(context)
        .enqueueUniquePeriodicWork(
            "weather_alerts",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
}

fun cancelWeatherAlerts(context: Context) {

    WorkManager
        .getInstance(context)
        .cancelUniqueWork("weather_alerts")
}