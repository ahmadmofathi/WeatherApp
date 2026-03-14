package com.example.weatherapp.fakes

import com.example.weatherapp.data.remote.WeatherRemoteDataSource
import com.example.weatherapp.data.remote.dto.*

class FakeWeatherRemoteDataSource : WeatherRemoteDataSource {

    var forecastResponse: ForecastResponse? = null
    var hourlyResponse: OneCallResponse? = null
    var dailyResponse: OneCallDailyResponse? = null
    var searchResults: List<GeoLocation> = emptyList()
    var shouldThrow: Boolean = false

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): ForecastResponse {
        if (shouldThrow) throw Exception("Network error")
        return forecastResponse ?: throw Exception("No forecast data set")
    }

    override suspend fun getWeatherByCity(
        city: String,
        units: String
    ): ForecastResponse {
        if (shouldThrow) throw Exception("Network error")
        return forecastResponse ?: throw Exception("No forecast data set")
    }

    override suspend fun getHourlyForecast(
        lat: Double,
        lon: Double,
        units: String
    ): OneCallResponse {
        if (shouldThrow) throw Exception("Network error")
        return hourlyResponse ?: throw Exception("No hourly data set")
    }

    override suspend fun getDailyForecast(
        lat: Double,
        lon: Double,
        units: String
    ): OneCallDailyResponse {
        if (shouldThrow) throw Exception("Network error")
        return dailyResponse ?: throw Exception("No daily data set")
    }

    override suspend fun searchCity(city: String): List<GeoLocation> {
        if (shouldThrow) throw Exception("Network error")
        return searchResults
    }
}
