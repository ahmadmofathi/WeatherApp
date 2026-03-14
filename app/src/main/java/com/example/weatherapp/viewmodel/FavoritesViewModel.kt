package com.example.weatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.favorite.FavoriteLocation
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.ui.favorites.FavoritesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesUiState>(
        FavoritesUiState.Loading
    )
    val uiState: StateFlow<FavoritesUiState> = _uiState

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    // Tracks which action just happened for snackbar
    private val _favoriteEvent = MutableStateFlow<FavoriteEvent?>(null)
    val favoriteEvent: StateFlow<FavoriteEvent?> = _favoriteEvent

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getFavorites()
                .catch { e ->
                    _uiState.value =
                        FavoritesUiState.Error(e.message ?: "Unknown error")
                }
                .collect { list ->
                    _uiState.value =
                        if (list.isEmpty()) {
                            FavoritesUiState.Empty
                        } else {
                            FavoritesUiState.Success(list)
                        }
                }
        }
    }

    fun checkIfFavorite(lat: Double, lon: Double) {
        val state = _uiState.value
        if (state is FavoritesUiState.Success) {
            _isFavorite.value = state.favorites.any { fav ->
                Math.abs(fav.latitude - lat) < 0.01 && Math.abs(fav.longitude - lon) < 0.01
            }
        } else {
            _isFavorite.value = false
        }
    }

    fun toggleFavorite(lat: Double, lon: Double) {
        val state = _uiState.value
        if (state is FavoritesUiState.Success) {
            val existing = state.favorites.find { fav ->
                Math.abs(fav.latitude - lat) < 0.01 && Math.abs(fav.longitude - lon) < 0.01
            }
            if (existing != null) {
                removeFavorite(existing)
                _isFavorite.value = false
                _favoriteEvent.value = FavoriteEvent.REMOVED
            } else {
                addFavoriteFromCoordinates(lat, lon)
                _isFavorite.value = true
                _favoriteEvent.value = FavoriteEvent.ADDED
            }
        } else {
            addFavoriteFromCoordinates(lat, lon)
            _isFavorite.value = true
            _favoriteEvent.value = FavoriteEvent.ADDED
        }
    }

    fun clearFavoriteEvent() {
        _favoriteEvent.value = null
    }

    fun addFavorite(location: FavoriteLocation) {
        viewModelScope.launch {
            repository.insert(location)
        }
    }

    fun removeFavorite(location: FavoriteLocation) {
        viewModelScope.launch {
            repository.delete(location)
        }
    }

    fun addFavoriteFromCoordinates(lat: Double, lon: Double) {

        viewModelScope.launch {

            try {

                val cityName =
                    repository.getCityName(
                        lat = lat,
                        lon = lon,
                        unit = "metric",
                        lang = "en"
                    )

                repository.insert(
                    FavoriteLocation(
                        cityName = cityName,
                        latitude = lat,
                        longitude = lon
                    )
                )

            } catch (e: Exception) {

                Log.e("WeatherError", e.toString())

            }
        }
    }
}

enum class FavoriteEvent {
    ADDED, REMOVED
}

class FavoritesViewModelFactory(
    private val repository: WeatherRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoritesViewModel(repository) as T
    }
}