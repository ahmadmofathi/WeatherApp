package com.example.weatherapp.datasource

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.WeatherLocalDataSourceImpl
import com.example.weatherapp.data.local.cache.CachedWeather
import com.example.weatherapp.data.local.favorite.FavoriteLocation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherLocalDataSourceTest {

    private lateinit var database: WeatherDatabase
    private lateinit var dataSource: WeatherLocalDataSourceImpl

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()

        dataSource = WeatherLocalDataSourceImpl(
            favoriteDao = database.favoriteDao(),
            cachedWeatherDao = database.cachedWeatherDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    // ── Favorite Tests ──────────────────────────────────────

    @Test
    fun insertFavorite_locationIsStored() = runTest {
        val location = FavoriteLocation(
            cityName = "Cairo",
            latitude = 30.0444,
            longitude = 31.2357
        )

        dataSource.insertFavorite(location)

        val favorites = dataSource.getFavorites().first()
        assertEquals(1, favorites.size)
        assertEquals("Cairo", favorites[0].cityName)
    }

    @Test
    fun deleteFavorite_locationIsRemoved() = runTest {
        val location = FavoriteLocation(
            cityName = "London",
            latitude = 51.5074,
            longitude = -0.1278
        )

        dataSource.insertFavorite(location)
        val stored = dataSource.getFavorites().first()[0]

        dataSource.deleteFavorite(stored)

        val favorites = dataSource.getFavorites().first()
        assertTrue(favorites.isEmpty())
    }

    @Test
    fun getFavorites_returnsAllInsertedLocations() = runTest {
        dataSource.insertFavorite(
            FavoriteLocation(cityName = "Cairo", latitude = 30.0, longitude = 31.0)
        )
        dataSource.insertFavorite(
            FavoriteLocation(cityName = "London", latitude = 51.5, longitude = -0.1)
        )
        dataSource.insertFavorite(
            FavoriteLocation(cityName = "Tokyo", latitude = 35.6, longitude = 139.7)
        )

        val favorites = dataSource.getFavorites().first()

        assertEquals(3, favorites.size)
        assertTrue(favorites.any { it.cityName == "Cairo" })
        assertTrue(favorites.any { it.cityName == "London" })
        assertTrue(favorites.any { it.cityName == "Tokyo" })
    }

    @Test
    fun getFavorites_emptyDatabaseReturnsEmptyList() = runTest {
        val favorites = dataSource.getFavorites().first()
        assertTrue(favorites.isEmpty())
    }

    @Test
    fun getFavorites_flowEmitsUpdatesOnInsert() = runTest {
        // Initial state — empty
        val initial = dataSource.getFavorites().first()
        assertTrue(initial.isEmpty())

        // Insert and verify emission
        dataSource.insertFavorite(
            FavoriteLocation(cityName = "Paris", latitude = 48.8, longitude = 2.3)
        )

        val afterInsert = dataSource.getFavorites().first()
        assertEquals(1, afterInsert.size)
        assertEquals("Paris", afterInsert[0].cityName)
    }

    // ── Cache Tests ─────────────────────────────────────────

    @Test
    fun saveWeather_cacheIsStored() = runTest {
        val cache = CachedWeather(
            latitude = 30.0,
            longitude = 31.0,
            cityName = "Cairo",
            temperature = 25.0,
            description = "Clear sky",
            humidity = 60,
            pressure = 1013,
            windSpeed = 3.5,
            rawJson = "{}",
            timestamp = System.currentTimeMillis()
        )

        dataSource.saveWeather(cache)

        val result = dataSource.getLastWeather()
        assertNotNull(result)
        assertEquals("Cairo", result!!.cityName)
        assertEquals(25.0, result.temperature, 0.01)
    }

    @Test
    fun getLastWeather_returnsNullWhenNoCacheExists() = runTest {
        val result = dataSource.getLastWeather()
        assertNull(result)
    }

    @Test
    fun saveWeather_overwritesPreviousCache() = runTest {
        val first = CachedWeather(
            latitude = 30.0, longitude = 31.0,
            cityName = "Cairo", temperature = 25.0,
            description = "Clear", humidity = 60,
            pressure = 1013, windSpeed = 3.5,
            rawJson = "{}", timestamp = 1000L
        )

        val second = CachedWeather(
            latitude = 40.0, longitude = 29.0,
            cityName = "Istanbul", temperature = 18.0,
            description = "Cloudy", humidity = 75,
            pressure = 1010, windSpeed = 5.2,
            rawJson = "{}", timestamp = 2000L
        )

        dataSource.saveWeather(first)
        dataSource.saveWeather(second)

        val result = dataSource.getLastWeather()
        assertNotNull(result)
        // Both have id=1, so second overwrites first
        assertEquals("Istanbul", result!!.cityName)
        assertEquals(18.0, result.temperature, 0.01)
    }
}
