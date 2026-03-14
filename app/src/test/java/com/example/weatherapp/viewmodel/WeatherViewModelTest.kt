package com.example.weatherapp.viewmodel

import com.example.weatherapp.data.remote.dto.*
import com.example.weatherapp.fakes.FakeNetworkMonitor
import com.example.weatherapp.fakes.FakeSettingsDataStore
import com.example.weatherapp.fakes.FakeWeatherRepository
import com.example.weatherapp.ui.weather.WeatherUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private lateinit var fakeRepository: FakeWeatherRepository
    private lateinit var fakeSettings: FakeSettingsDataStore
    private lateinit var fakeNetwork: FakeNetworkMonitor
    private lateinit var viewModel: WeatherViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testForecast = ForecastResponse(
        coord = Coord(lat = 30.0, lon = 31.0),
        weather = listOf(Weather(id = 800, main = "Clear", description = "clear sky", icon = "01d")),
        base = "stations",
        main = Main(temp = 25.0, feels_like = 24.0, temp_min = 22.0, temp_max = 28.0, pressure = 1013, humidity = 50),
        visibility = 10000,
        wind = Wind(speed = 5.0, deg = 180, gust = null),
        rain = null,
        clouds = Clouds(all = 0),
        dt = 1700000000L,
        sys = Sys(type = 1, id = 1, country = "EG", sunrise = 1700000000L, sunset = 1700040000L),
        timezone = 7200,
        id = 360630,
        name = "Cairo",
        cod = 200
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeWeatherRepository()
        fakeSettings = FakeSettingsDataStore()
        fakeNetwork = FakeNetworkMonitor()
        viewModel = WeatherViewModel(fakeRepository, fakeSettings, fakeNetwork)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── Test 1: loadWeather updates UI state with weather data ───

    @Test
    fun `loadWeather updates uiState to Success with weather data`() = runTest {
        fakeRepository.forecastResponse = testForecast
        fakeRepository.hourlyResponse = OneCallResponse(hourly = emptyList())
        fakeRepository.dailyResponse = OneCallDailyResponse(daily = emptyList())

        viewModel.loadWeather(30.0, 31.0)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Success)
        assertEquals("Cairo", (state as WeatherUiState.Success).weather.name)
        assertEquals(25.0, state.weather.main.temp, 0.01)
    }

    @Test
    fun `loadWeather sets Error state when repository throws`() = runTest {
        fakeRepository.shouldThrow = true

        viewModel.loadWeather(30.0, 31.0)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Error)
    }

    @Test
    fun `loadWeather sets Offline state when repository throws but cache exists`() = runTest {
        fakeRepository.shouldThrow = true
        fakeRepository.cachedResponse = testForecast

        viewModel.loadWeather(30.0, 31.0)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Offline)
        assertEquals("Cairo", (state as WeatherUiState.Offline).weather.name)
    }

    // ─── Test 2: loadWeatherByCity loads correct weather ───

    @Test
    fun `loadWeatherByCity updates uiState with correct data`() = runTest {
        fakeRepository.forecastResponse = testForecast
        fakeRepository.hourlyResponse = OneCallResponse(hourly = emptyList())
        fakeRepository.dailyResponse = OneCallDailyResponse(daily = emptyList())

        viewModel.loadWeatherByCity("Cairo")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Success)
        assertEquals("Cairo", (state as WeatherUiState.Success).weather.name)
    }

    // ─── Test 3: reloadWeather triggers new repository call ───

    @Test
    fun `reloadWeather calls repository with last known coordinates`() = runTest {
        fakeRepository.forecastResponse = testForecast
        fakeRepository.hourlyResponse = OneCallResponse(hourly = emptyList())
        fakeRepository.dailyResponse = OneCallDailyResponse(daily = emptyList())

        viewModel.loadWeather(30.0, 31.0)
        advanceUntilIdle()

        val countAfterFirst = fakeRepository.getWeatherCallCount

        viewModel.reloadWeather()
        advanceUntilIdle()

        assertTrue(fakeRepository.getWeatherCallCount > countAfterFirst)
        assertEquals(30.0, fakeRepository.lastGetWeatherLat!!, 0.01)
        assertEquals(31.0, fakeRepository.lastGetWeatherLon!!, 0.01)
    }

    // ─── Test 4: ViewModel exposes correct state flow ───

    @Test
    fun `uiState starts as Loading`() {
        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Loading)
    }

    @Test
    fun `isRefreshing is false after loading completes`() = runTest {
        fakeRepository.forecastResponse = testForecast
        fakeRepository.hourlyResponse = OneCallResponse(hourly = emptyList())
        fakeRepository.dailyResponse = OneCallDailyResponse(daily = emptyList())

        viewModel.loadWeather(30.0, 31.0)
        advanceUntilIdle()

        assertFalse(viewModel.isRefreshing.value)
    }

    @Test
    fun `getLastLocation returns correct coordinates after loadWeather`() = runTest {
        fakeRepository.forecastResponse = testForecast
        fakeRepository.hourlyResponse = OneCallResponse(hourly = emptyList())
        fakeRepository.dailyResponse = OneCallDailyResponse(daily = emptyList())

        viewModel.loadWeather(30.0, 31.0)
        advanceUntilIdle()

        val last = viewModel.getLastLocation()
        assertEquals(30.0, last.first, 0.01)
        assertEquals(31.0, last.second, 0.01)
    }
}
