package com.example.weatherapp.viewmodel

import com.example.weatherapp.data.local.favorite.FavoriteLocation
import com.example.weatherapp.data.remote.dto.*
import com.example.weatherapp.fakes.FakeWeatherRepository
import com.example.weatherapp.ui.favorites.FavoritesUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private lateinit var fakeRepository: FakeWeatherRepository
    private lateinit var viewModel: FavoritesViewModel

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
        fakeRepository.forecastResponse = testForecast
        viewModel = FavoritesViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── Test 1: addFavorite inserts new location ───

    @Test
    fun `addFavorite inserts location into repository`() = runTest {
        val location = FavoriteLocation(
            id = 0,
            cityName = "Alexandria",
            latitude = 31.2,
            longitude = 29.9
        )

        viewModel.addFavorite(location)
        advanceUntilIdle()

        val favorites = fakeRepository.getFavoritesList()
        assertEquals(1, favorites.size)
        assertEquals("Alexandria", favorites[0].cityName)
    }

    // ─── Test 2: removeFavorite deletes location ───

    @Test
    fun `removeFavorite removes location from repository`() = runTest {
        val location = FavoriteLocation(
            id = 1,
            cityName = "Giza",
            latitude = 30.0,
            longitude = 31.2
        )

        viewModel.addFavorite(location)
        advanceUntilIdle()
        assertEquals(1, fakeRepository.getFavoritesList().size)

        viewModel.removeFavorite(location)
        advanceUntilIdle()
        assertEquals(0, fakeRepository.getFavoritesList().size)
    }

    // ─── Test 3: favorites Flow emits updated list ───

    @Test
    fun `uiState starts as Empty when no favorites`() = runTest {
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(
            "Expected Empty but was $state",
            state is FavoritesUiState.Empty
        )
    }

    @Test
    fun `uiState changes to Success after adding favorite`() = runTest {
        val location = FavoriteLocation(
            id = 0,
            cityName = "Luxor",
            latitude = 25.7,
            longitude = 32.6
        )

        viewModel.addFavorite(location)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(
            "Expected Success but was $state",
            state is FavoritesUiState.Success
        )
        assertEquals(1, (state as FavoritesUiState.Success).favorites.size)
        assertEquals("Luxor", state.favorites[0].cityName)
    }

    @Test
    fun `uiState changes back to Empty after removing all favorites`() = runTest {
        val location = FavoriteLocation(
            id = 0,
            cityName = "Aswan",
            latitude = 24.0,
            longitude = 32.9
        )

        viewModel.addFavorite(location)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is FavoritesUiState.Success)

        viewModel.removeFavorite(location)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is FavoritesUiState.Empty)
    }

    @Test
    fun `adding multiple favorites updates uiState with all items`() = runTest {
        val loc1 = FavoriteLocation(0, "Cairo", 30.0, 31.0)
        val loc2 = FavoriteLocation(0, "Luxor", 25.7, 32.6)
        val loc3 = FavoriteLocation(0, "Aswan", 24.0, 32.9)

        viewModel.addFavorite(loc1)
        viewModel.addFavorite(loc2)
        viewModel.addFavorite(loc3)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FavoritesUiState.Success)
        assertEquals(3, (state as FavoritesUiState.Success).favorites.size)
    }
}
