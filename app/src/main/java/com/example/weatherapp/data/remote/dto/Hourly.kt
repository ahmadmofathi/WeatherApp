package com.example.weatherapp.data.remote.dto

data class Hourly(
    val dt: Long,
    val temp: Double,
    val weather: List<Weather>
)