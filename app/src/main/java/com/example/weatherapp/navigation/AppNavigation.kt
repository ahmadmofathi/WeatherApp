package com.example.weatherapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import com.example.weatherapp.data.preferences.SettingsDataStore
import com.example.weatherapp.ui.favorites.FavoritesScreen
import com.example.weatherapp.ui.home.HomeScreen
import com.example.weatherapp.ui.map.MapScreen
import com.example.weatherapp.ui.settings.SettingsScreen
import com.example.weatherapp.utils.scheduleWeatherAlerts
import com.example.weatherapp.viewmodel.FavoritesViewModel
import com.example.weatherapp.viewmodel.SettingsViewModel
import com.example.weatherapp.viewmodel.SettingsViewModelFactory
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


        //SETTINGS
        composable(Screen.Settings.route) {

            val context = LocalContext.current

            val dataStore = remember {
                SettingsDataStore(context)
            }

            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(dataStore)
            )
            val alertsEnabled by settingsViewModel.alertsEnabled.collectAsState()
            val unit by settingsViewModel.temperatureUnit.collectAsState()
            val language by settingsViewModel.language.collectAsState()


            SettingsScreen(

                alertsEnabled = alertsEnabled,
                temperatureUnit = unit,
                language = language,

                onAlertsChanged = { enabled ->

                    settingsViewModel.setAlertsEnabled(enabled)

                    val location = weatherViewModel.getLastLocation()
                    val lat = location.first
                    val lon = location.second

                    if (enabled) {

                        scheduleWeatherAlerts(context, lat, lon)

                    } else {

                        WorkManager.getInstance(context)
                            .cancelUniqueWork("weather_alerts")
                    }
                },

                onUnitChanged = { unit ->
                    settingsViewModel.setTemperatureUnit(unit)
                    weatherViewModel.reloadWeather()
                },

                onLanguageChanged = { lang ->
                    settingsViewModel.setLanguage(lang)
                    weatherViewModel.reloadWeather()
                }
            )
        }
    }
}