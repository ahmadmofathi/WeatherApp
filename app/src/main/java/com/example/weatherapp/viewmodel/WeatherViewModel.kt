package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.preferences.SettingsProvider
import com.example.weatherapp.data.remote.dto.Daily
import com.example.weatherapp.data.remote.dto.Hourly
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.ui.weather.WeatherUiState
import com.example.weatherapp.utils.ConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val settingsDataStore: SettingsProvider,
    private val networkMonitor: ConnectivityObserver
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _hourlyForecast =
        MutableStateFlow<List<Hourly>>(emptyList())
    val hourlyForecast: StateFlow<List<Hourly>> = _hourlyForecast

    private val _dailyForecast =
        MutableStateFlow<List<Daily>>(emptyList())
    val dailyForecast: StateFlow<List<Daily>> = _dailyForecast

    private val _location =
        MutableStateFlow<Pair<Double, Double>?>(null)

    val location: StateFlow<Pair<Double, Double>?> = _location
    private var locationInitialized = false

    val isConnected: StateFlow<Boolean> = networkMonitor.isConnected

    private var lastLat: Double = 0.0
    private var lastLon: Double = 0.0

    private var unit: String = "metric"
    private var windUnit: String = "mps"
    private var language: String = "en"

    // Expose units for UI components
    private val _temperatureUnit = MutableStateFlow("metric")
    val temperatureUnit: StateFlow<String> = _temperatureUnit

    private val _windSpeedUnit = MutableStateFlow("mps")
    val windSpeedUnit: StateFlow<String> = _windSpeedUnit

    init {

        viewModelScope.launch {
            settingsDataStore.temperatureUnit.collect {
                unit = it
                _temperatureUnit.value = it
            }
        }

        viewModelScope.launch {
            settingsDataStore.windSpeedUnit.collect {
                windUnit = it
                _windSpeedUnit.value = it
            }
        }

        viewModelScope.launch {
            settingsDataStore.language.collect {
                language = it
            }
        }
    }

    fun loadWeather(lat: Double, lon: Double) {

        lastLat = lat
        lastLon = lon

        viewModelScope.launch {

            _isRefreshing.value = true

            try {

                // Map "kelvin" to "standard" for OpenWeather API
                val apiUnit = if (unit == "kelvin") "standard" else unit

                val response =
                    repository.getWeather(
                        lat,
                        lon,
                        apiUnit,
                        language
                    )

                _uiState.value =
                    WeatherUiState.Success(response)

                // Try to load forecasts (non-critical)
                try {
                    val hourly =
                        repository.getHourlyForecast(
                            lat,
                            lon,
                            apiUnit
                        )

                    _hourlyForecast.value =
                        hourly.hourly.take(12)

                    val daily =
                        repository.getDailyForecast(
                            lat,
                            lon,
                            apiUnit
                        )

                    _dailyForecast.value =
                        daily.daily.take(5)
                } catch (_: Exception) {
                    // Forecasts are non-critical; main weather is already showing
                }

            } catch (e: Exception) {

                // API failed — try to load cached weather
                val cached = repository.getCachedWeather()

                if (cached != null) {
                    _uiState.value = WeatherUiState.Offline(cached)
                } else {
                    _uiState.value =
                        WeatherUiState.Error(
                            e.message ?: "No internet connection"
                        )
                }
            }

            _isRefreshing.value = false
        }

    }

    fun setLocation(lat: Double, lon: Double) {
        lastLat = lat
        lastLon = lon
        _location.value = Pair(lat, lon)
        loadWeather(lat, lon)
    }

    fun getLastLocation(): Pair<Double, Double> {
        return Pair(lastLat, lastLon)
    }

    fun reloadWeather() {
        loadWeather(lastLat, lastLon)
    }

    fun loadWeatherByCity(city: String) {

        viewModelScope.launch {

            try {
                val response = repository.getWeatherByCity(city)

                loadWeather(
                    response.coord.lat,
                    response.coord.lon
                )
            } catch (e: Exception) {
                val cached = repository.getCachedWeather()
                if (cached != null) {
                    _uiState.value = WeatherUiState.Offline(cached)
                } else {
                    _uiState.value = WeatherUiState.Error(
                        e.message ?: "No internet connection"
                    )
                }
            }
        }
    }

    fun initializeLocation(lat: Double, lon: Double) {
        if (!locationInitialized) {
            setLocation(lat, lon)
            locationInitialized = true
        }
    }

    fun setLocationDenied() {
        // Only show denied state if no location has been set yet
        if (!locationInitialized && _location.value == null) {
            _uiState.value = WeatherUiState.LocationDenied
        }
    }
}


class WeatherViewModelFactory(
    private val repository: WeatherRepository,
    private val settingsDataStore: SettingsProvider,
    private val networkMonitor: ConnectivityObserver
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {

            return WeatherViewModel(
                repository,
                settingsDataStore,
                networkMonitor
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}