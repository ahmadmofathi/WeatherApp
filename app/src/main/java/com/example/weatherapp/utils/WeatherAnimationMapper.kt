package com.example.weatherapp.utils

import com.example.weatherapp.R
import com.example.weatherapp.model.WeatherCondition

/**
 * Maps weather conditions to Lottie animation resources.
 * Provides a centralized mapping for animations throughout the app.
 */
object WeatherAnimationMapper {

    /**
     * Get the Lottie animation resource for a weather condition.
     */
    fun getAnimationRes(condition: WeatherCondition): Int {
        return when (condition) {
            WeatherCondition.CLEAR -> R.raw.sunny
            WeatherCondition.CLOUDS -> R.raw.cloudy
            WeatherCondition.RAIN, WeatherCondition.DRIZZLE -> R.raw.rain
            WeatherCondition.THUNDERSTORM -> R.raw.thunder
            WeatherCondition.SNOW -> R.raw.snow
            WeatherCondition.MIST, WeatherCondition.FOG, WeatherCondition.HAZE -> R.raw.fog
            WeatherCondition.SMOKE, WeatherCondition.DUST, WeatherCondition.SAND,
            WeatherCondition.ASH -> R.raw.smoke
            WeatherCondition.SQUALL, WeatherCondition.TORNADO, WeatherCondition.WIND -> R.raw.wind
        }
    }

    /**
     * Get the Lottie animation resource from an API condition name string.
     */
    fun getAnimationRes(apiName: String): Int {
        val condition = WeatherCondition.fromApiNameOrDefault(apiName)
        return getAnimationRes(condition)
    }
}
