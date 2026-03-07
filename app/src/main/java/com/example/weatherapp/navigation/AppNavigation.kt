package com.example.weatherapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.favorites.FavoritesScreen
import com.example.weatherapp.ui.home.HomeScreen
import com.example.weatherapp.ui.map.MapScreen
import com.example.weatherapp.viewmodel.FavoritesViewModel
import com.example.weatherapp.viewmodel.WeatherViewModel

@Composable
fun AppNavigation(
    favoritesViewModel: FavoritesViewModel,
    weatherViewModel: WeatherViewModel
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {

        // HOME
        composable(Screen.Home.route) {

            HomeScreen(
                favoritesViewModel = favoritesViewModel,
                weatherViewModel = weatherViewModel,
                navController = navController
            )
        }

        // FAVORITES
        composable(Screen.Favorites.route) {

            FavoritesScreen(
                viewModel = favoritesViewModel,
                onAddClick = {
                    navController.navigate(Screen.Map.route)
                },
                onFavoriteClick = { lat, lon ->

                    weatherViewModel.loadWeather(lat, lon)

                    navController.popBackStack()
                }
            )
        }

        // MAP
        composable(Screen.Map.route) {

            MapScreen { lat, lon ->

                favoritesViewModel.addFavoriteFromCoordinates(lat, lon)

                navController.popBackStack()
            }
        }
    }
}