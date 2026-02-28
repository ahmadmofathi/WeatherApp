package com.example.weatherapp.data.remote.dto

data class ForecastResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherItem>,
    val city: City
)
