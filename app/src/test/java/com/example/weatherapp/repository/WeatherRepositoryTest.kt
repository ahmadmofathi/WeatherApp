package com.example.weatherapp.repository

import com.example.weatherapp.data.local.favorite.FavoriteLocation
import com.example.weatherapp.data.remote.dto.*
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.fakes.FakeWeatherLocalDataSource
import com.example.weatherapp.fakes.FakeWeatherRemoteDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest {

    private lateinit var fakeRemote: FakeWeatherRemoteDataSource
    private lateinit var fakeLocal: FakeWeatherLocalDataSource
    private lateinit var repository: WeatherRepositoryImpl

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
        fakeRemote = FakeWeatherRemoteDataSource()
        fakeLocal = FakeWeatherLocalDataSource()
        repository = WeatherRepositoryImpl(fakeLocal, fakeRemote)
    }

    // ─── Test 1: getWeather returns expected data from RemoteDataSource ───

    @Test
    fun `getWeather returns forecast from remote data source`() = runTest {
        fakeRemote.forecastResponse = testForecast

        val result = repository.getWeather(30.0, 31.0, "metric", "en")

        assertEquals("Cairo", result.name)
        assertEquals(25.0, result.main.temp, 0.01)
        assertEquals("Clear", result.weather[0].main)
    }

    @Test
    fun `getWeather caches the response in local data source`() = runTest {
        fakeRemote.forecastResponse = testForecast

        repository.getWeather(30.0, 31.0, "metric", "en")

        assertNotNull(fakeLocal.savedCache)
        assertEquals("Cairo", fakeLocal.savedCache!!.cityName)
        assertEquals(30.0, fakeLocal.savedCache!!.latitude, 0.01)
    }

    // ─── Test 2: addFavorite inserts location into LocalDataSource ───

    @Test
    fun `insert adds favorite to local data source`() = runTest {
        val location = FavoriteLocation(
            id = 0,
            cityName = "Alexandria",
            latitude = 31.2,
            longitude = 29.9
        )

        repository.insert(location)

        val favorites = fakeLocal.getFavoritesList()
        assertEquals(1, favorites.size)
        assertEquals("Alexandria", favorites[0].cityName)
    }

    // ─── Test 3: removeFavorite deletes location ───

    @Test
    fun `delete removes favorite from local data source`() = runTest {
        val location = FavoriteLocation(
            id = 1,
            cityName = "Giza",
            latitude = 30.0,
            longitude = 31.2
        )

        repository.insert(location)
        assertEquals(1, fakeLocal.getFavoritesList().size)

        repository.delete(location)
        assertEquals(0, fakeLocal.getFavoritesList().size)
    }

    // ─── Test 4: getFavorites returns correct Flow data ───

    @Test
    fun `getFavorites returns flow with inserted favorites`() = runTest {
        val loc1 = FavoriteLocation(0, "Cairo", 30.0, 31.0)
        val loc2 = FavoriteLocation(0, "Luxor", 25.7, 32.6)

        repository.insert(loc1)
        repository.insert(loc2)

        val favorites = repository.getFavorites().first()
        assertEquals(2, favorites.size)
        assertEquals("Cairo", favorites[0].cityName)
        assertEquals("Luxor", favorites[1].cityName)
    }

    @Test
    fun `getFavorites returns empty list initially`() = runTest {
        val favorites = repository.getFavorites().first()
        assertTrue(favorites.isEmpty())
    }

    // ─── Test 5: repository delegates calls to correct data source ───

    @Test
    fun `getCityName delegates to remote data source`() = runTest {
        fakeRemote.forecastResponse = testForecast

        val cityName = repository.getCityName(30.0, 31.0, "metric", "en")

        assertEquals("Cairo", cityName)
    }

    @Test
    fun `getWeatherByCity delegates to remote data source`() = runTest {
        fakeRemote.forecastResponse = testForecast

        val result = repository.getWeatherByCity("Cairo")

        assertEquals("Cairo", result.name)
        assertEquals(200, result.cod)
    }

    @Test
    fun `searchCity delegates to remote data source`() = runTest {
        fakeRemote.searchResults = listOf(
            GeoLocation(name = "Cairo", lat = 30.0, lon = 31.0, country = "EG"),
            GeoLocation(name = "Casablanca", lat = 33.5, lon = -7.6, country = "MA")
        )

        val results = repository.searchCity("Ca")

        assertEquals(2, results.size)
        assertEquals("Cairo", results[0].name)
        assertEquals("Casablanca", results[1].name)
    }

    @Test
    fun `getHourlyForecast delegates to remote data source`() = runTest {
        fakeRemote.hourlyResponse = OneCallResponse(hourly = emptyList())

        val result = repository.getHourlyForecast(30.0, 31.0, "metric")

        assertNotNull(result)
        assertTrue(result.hourly.isEmpty())
    }

    @Test
    fun `getDailyForecast delegates to remote data source`() = runTest {
        fakeRemote.dailyResponse = OneCallDailyResponse(daily = emptyList())

        val result = repository.getDailyForecast(30.0, 31.0, "metric")

        assertNotNull(result)
        assertTrue(result.daily.isEmpty())
    }

    @Test
    fun `getCachedWeather returns null when no cache exists`() = runTest {
        val result = repository.getCachedWeather()
        assertNull(result)
    }
}
