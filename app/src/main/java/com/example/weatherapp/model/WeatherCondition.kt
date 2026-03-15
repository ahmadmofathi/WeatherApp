package com.example.weatherapp.model

import com.example.weatherapp.R

/**
 * Centralized weather conditions from OpenWeatherMap API.
 * Maps conditions to display names, emojis, and string resources.
 */
enum class WeatherCondition(
    val apiName: String,
    val emoji: String,
    val labelResId: Int
) {
    CLEAR("Clear", "☀️", R.string.clear),
    CLOUDS("Clouds", "☁️", R.string.clouds),
    RAIN("Rain", "🌧️", R.string.rain),
    DRIZZLE("Drizzle", "🌦️", R.string.drizzle),
    THUNDERSTORM("Thunderstorm", "⛈️", R.string.thunderstorm),
    SNOW("Snow", "❄️", R.string.snow),
    MIST("Mist", "🌫️", R.string.mist),
    SMOKE("Smoke", "💨", R.string.smoke),
    HAZE("Haze", "🌫️", R.string.haze),
    DUST("Dust", "🏜️", R.string.dust),
    FOG("Fog", "🌫️", R.string.fog),
    SAND("Sand", "🏜️", R.string.sand),
    ASH("Ash", "🌋", R.string.ash),
    SQUALL("Squall", "💨", R.string.squall),
    TORNADO("Tornado", "🌪️", R.string.tornado),
    WIND("Wind", "💨", R.string.wind);

    companion object {
        /**
         * Find a WeatherCondition by its OpenWeatherMap API name.
         * Returns null if not found.
         */
        fun fromApiName(name: String): WeatherCondition? =
            entries.find { it.apiName.equals(name, ignoreCase = true) }

        /**
         * Get a condition with a default fallback.
         */
        fun fromApiNameOrDefault(name: String): WeatherCondition =
            fromApiName(name) ?: CLOUDS

        /**
         * Conditions available for user selection in alerts/notifications.
         */
        val selectableConditions = listOf(
            CLEAR, CLOUDS, RAIN, DRIZZLE, THUNDERSTORM,
            SNOW, FOG, MIST, DUST, WIND
        )
    }
}
