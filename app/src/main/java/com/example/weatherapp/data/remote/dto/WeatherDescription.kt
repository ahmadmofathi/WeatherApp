package com.example.weatherapp.data.remote.dto

data class WeatherDescription (
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)