package com.example.weatherapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("1h")
    val oneHour: Double
)