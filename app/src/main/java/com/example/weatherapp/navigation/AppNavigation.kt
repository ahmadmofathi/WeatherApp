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
import com.example.weatherapp.utils.getCityName
@Composable
fun AppNavigation(
    viewModel: FavoritesViewModel
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Favorites.route
    ) {

        composable(Screen.Favorites.route) {

            FavoritesScreen(
                viewModel = viewModel,
                onAddClick = {
                    navController.navigate(Screen.Map.route)
                }
            )
        }

        composable(Screen.Map.route) {

            val context = LocalContext.current

            MapScreen { lat, lon ->

                val cityName = getCityName(context, lat, lon)

                viewModel.addFavorite(
                    FavoriteLocation(
                        cityName = cityName,
                        latitude = lat,
                        longitude = lon
                    )
                )

                navController.popBackStack()
            }
        }
    }
}