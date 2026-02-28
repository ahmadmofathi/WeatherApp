package com.example.weatherapp.data.remote.dto

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val timezone: Int,
)
