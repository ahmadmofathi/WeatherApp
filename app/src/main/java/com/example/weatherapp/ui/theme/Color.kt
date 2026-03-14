package com.example.weatherapp.ui.theme

import androidx.compose.ui.graphics.Color

// Light theme colors
val Blue40 = Color(0xFF1565C0)
val BlueGrey40 = Color(0xFF546E7A)
val Teal40 = Color(0xFF00897B)

// Dark theme colors
val Blue80 = Color(0xFF90CAF9)
val BlueGrey80 = Color(0xFFB0BEC5)
val Teal80 = Color(0xFF80CBC4)

// Weather gradients
object WeatherColors {
    val ClearDay = listOf(Color(0xFF56CCF2), Color(0xFF2F80ED))
    val ClearNight = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
    val Clouds = listOf(Color(0xFF757F9A), Color(0xFFD7DDE8))
    val Rain = listOf(Color(0xFF1A2980), Color(0xFF26D0CE))
    val Snow = listOf(Color(0xFFE6DADA), Color(0xFF274046))
    val Thunderstorm = listOf(Color(0xFF232526), Color(0xFF414345))
    val Default = listOf(Color(0xFF4FC3F7), Color(0xFF0288D1))

    // Glassmorphism
    val GlassWhite = Color.White.copy(alpha = 0.15f)
    val GlassBorder = Color.White.copy(alpha = 0.25f)
    val GlassWhiteStrong = Color.White.copy(alpha = 0.22f)
    val OnGlass = Color.White
    val OnGlassSecondary = Color.White.copy(alpha = 0.7f)

    // Offline banner
    val OfflineBannerBg = Color(0xFFFFF3E0)
    val OfflineBannerText = Color(0xFFE65100)
}