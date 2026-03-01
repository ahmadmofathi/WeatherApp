package com.example.weatherapp.viewmodel

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
}


class FavoritesViewModelFactory(
    private val repository: WeatherRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoritesViewModel(repository) as T
    }
}