package com.example.weatherapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.favorites.FavoritesScreen
import com.example.weatherapp.ui.map.MapScreen
import com.example.weatherapp.viewmodel.FavoritesViewModel
import com.example.weatherapp.data.local.favorite.FavoriteLocation
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.ui.weather.WeatherScreen
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.example.weatherapp.viewmodel.WeatherViewModelFactory

@Composable
fun AppNavigation(
    favoritesViewModel: FavoritesViewModel,
    weatherViewModel: WeatherViewModel
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Favorites.route
    ) {

        composable(Screen.Favorites.route) {

            FavoritesScreen(
                viewModel = favoritesViewModel,
                onAddClick = {
                    navController.navigate(Screen.Map.route)
                },
                onFavoriteClick = { lat, lon ->
                    navController.navigate("weather/$lat/$lon")
                }
            )
        }

        composable(Screen.Map.route) {

            val context = LocalContext.current

            MapScreen { lat, lon ->

                favoritesViewModel.addFavoriteFromCoordinates(lat, lon)

                navController.popBackStack()
            }
        }

        composable(
            route = "weather/{lat}/{lon}"
        ) { backStackEntry ->

            val lat =
                backStackEntry.arguments
                    ?.getString("lat")!!
                    .toDouble()

            val lon =
                backStackEntry.arguments
                    ?.getString("lon")!!
                    .toDouble()


            WeatherScreen(
                lat = lat,
                lon = lon,
                viewModel = weatherViewModel
            )
        }
    }
}