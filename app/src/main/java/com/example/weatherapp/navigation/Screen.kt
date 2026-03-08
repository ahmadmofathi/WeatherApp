package com.example.weatherapp.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")
    object Map : Screen("map")

    object Weather : Screen("weather/{lat}/{lon}")

}