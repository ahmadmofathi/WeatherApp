package com.example.weatherapp.data.remote.dto

data class WeatherItem(
    val dt: Int,
    val main: MainInfo,
    val weather: List<WeatherDescription>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val dt_txt: String
)
