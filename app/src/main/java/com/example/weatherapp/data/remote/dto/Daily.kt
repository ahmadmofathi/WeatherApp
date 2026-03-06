package com.example.weatherapp.data.remote.dto

data class Daily(
    val dt: Long,
    val temp: Temp,
    val weather: List<Weather>
)