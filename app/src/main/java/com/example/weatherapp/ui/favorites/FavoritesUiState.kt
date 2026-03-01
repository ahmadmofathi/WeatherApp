package com.example.weatherapp.ui.favorites

import com.example.weatherapp.data.local.favorite.FavoriteLocation

sealed class FavoritesUiState{
    object Loading: FavoritesUiState()
    data class Success(val favorites: List<FavoriteLocation>): FavoritesUiState()
    object Empty: FavoritesUiState()
    data class Error (val message: String): FavoritesUiState()
}