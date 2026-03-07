package com.example.weatherapp.utils

import java.text.SimpleDateFormat
import java.util.*

fun getCurrentDate(): String {
    val formatter = SimpleDateFormat("EEEE, d MMM", Locale.getDefault())
    return formatter.format(Date())
}

fun getCurrentTime(): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(Date())
}