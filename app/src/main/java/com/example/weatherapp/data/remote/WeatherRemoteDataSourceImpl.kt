package com.example.weatherapp.data.remote

import com.example.weatherapp.data.remote.dto.ForecastResponse
import com.example.weatherapp.data.remote.dto.GeoLocation
import com.example.weatherapp.data.remote.dto.OneCallDailyResponse
import com.example.weatherapp.data.remote.dto.OneCallResponse

class WeatherRemoteDataSourceImpl(
    private val api: WeatherApi
) : WeatherRemoteDataSource {

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): ForecastResponse {
        return api.getForecast(lat, lon, units, lang)
    }

    override suspend fun getWeatherByCity(
        city: String,
        units: String
    ): ForecastResponse {
        return api.getWeatherByCity(city, units)
    }

    override suspend fun getHourlyForecast(
        lat: Double,
        lon: Double,
        units: String
    ): OneCallResponse {
        return api.getHourlyForecast(lat, lon, units)
    }

    override suspend fun getDailyForecast(
        lat: Double,
        lon: Double,
        units: String
    ): OneCallDailyResponse {
        return api.getDailyForecast(lat, lon, units)
    }

    override suspend fun searchCity(city: String): List<GeoLocation> {
        return api.searchCity(city)
    }
}
