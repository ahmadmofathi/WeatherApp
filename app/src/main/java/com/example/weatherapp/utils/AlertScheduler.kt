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

    val request =
        PeriodicWorkRequestBuilder<WeatherAlertWorker>(
            30, TimeUnit.MINUTES
        )
            .setInputData(data)
            .build()

    WorkManager
        .getInstance(context)
        .enqueueUniquePeriodicWork(
            "weather_alerts",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
}