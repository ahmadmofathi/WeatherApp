package com.example.weatherapp.navigation

sealed class Screen(val route: String) {
    object Favorites : Screen("favorites")
}