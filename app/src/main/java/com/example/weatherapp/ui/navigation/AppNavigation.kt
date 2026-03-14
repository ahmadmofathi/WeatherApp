package com.example.weatherapp.navigation

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.data.local.alert.WeatherAlert
import com.example.weatherapp.data.preferences.SettingsDataStore
import com.example.weatherapp.ui.alerts.AlertsScreen
import com.example.weatherapp.ui.favorites.FavoritesScreen
import com.example.weatherapp.ui.home.HomeScreen
import com.example.weatherapp.ui.map.MapScreen
import com.example.weatherapp.ui.search.SearchScreen
import com.example.weatherapp.ui.settings.SettingsScreen
import com.example.weatherapp.utils.cancelAlarm
import com.example.weatherapp.utils.scheduleAlarm
import com.example.weatherapp.viewmodel.AlertViewModel
import com.example.weatherapp.viewmodel.FavoritesViewModel
import com.example.weatherapp.viewmodel.SearchViewModel
import com.example.weatherapp.viewmodel.SettingsViewModel
import com.example.weatherapp.viewmodel.SettingsViewModelFactory
import com.example.weatherapp.viewmodel.WeatherViewModel

@Composable
fun AppNavigation(
    favoritesViewModel: FavoritesViewModel,
    weatherViewModel: WeatherViewModel,
    searchViewModel: SearchViewModel,
    alertViewModel: AlertViewModel
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
                    weatherViewModel.setLocation(lat, lon)
                    weatherViewModel.loadWeather(lat, lon)

                    navController.popBackStack()
                }
            )
        }

        // MAP
        composable(Screen.Map.route) {

            MapScreen { lat, lon ->

                weatherViewModel.setLocation(lat, lon)

                weatherViewModel.loadWeather(lat, lon)

                navController.navigate(Screen.Home.route)
            }
        }

        //SEARCH
        composable(Screen.Search.route) {

            SearchScreen(

                viewModel = searchViewModel,

                onCitySelected = { lat, lon ->

                    weatherViewModel.setLocation(lat, lon)

                    weatherViewModel.loadWeather(lat, lon)

                    navController.popBackStack()
                },

                onMapClick = {

                    navController.navigate(Screen.Map.route)

                }
            )
        }
        composable("weatherByName/{city}") { backStackEntry ->

            val city =
                backStackEntry.arguments?.getString("city")!!

            LaunchedEffect(city) {
                weatherViewModel.loadWeatherByCity(city)
            }

            HomeScreen(
                favoritesViewModel = favoritesViewModel,
                weatherViewModel = weatherViewModel,
                navController = navController
            )
        }

        //Alerts
        composable(Screen.Alerts.route) {

            val context = LocalContext.current
            val alertList by alertViewModel.alerts.collectAsState()

            AlertsScreen(

                alerts = alertList,

                onSaveAlert = { condition, start, end ->

                    val location =
                        weatherViewModel.getLastLocation()

                    val lat = location.first
                    val lon = location.second

                    val alert = WeatherAlert(
                        latitude = lat,
                        longitude = lon,
                        condition = condition,
                        startTime = start,
                        endTime = end
                    )

                    alertViewModel.addAlert(alert) { generatedId ->
                        scheduleAlarm(
                            context = context,
                            triggerTime = start,
                            alertId = generatedId.toInt()
                        )
                    }
                },

                onDeleteAlert = { alert ->
                    cancelAlarm(
                        context = context,
                        alertId = alert.id
                    )
                    alertViewModel.deleteAlert(alert)
                }
            )
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
            val alertCondition by settingsViewModel.alertCondition.collectAsState()
            val unit by settingsViewModel.temperatureUnit.collectAsState()
            val language by settingsViewModel.language.collectAsState()


            SettingsScreen(

                alertsEnabled = alertsEnabled,
                alertCondition = alertCondition,
                temperatureUnit = unit,
                language = language,

                onAlertsChanged = { enabled ->

                    val location = weatherViewModel.getLastLocation()
                    val lat = location.first
                    val lon = location.second

                    settingsViewModel.toggleAlerts(
                        context = context,
                        enabled = enabled,
                        lat = lat,
                        lon = lon
                    )
                },

                onConditionChanged = { condition ->
                    settingsViewModel.setAlertCondition(condition)
                },

                onUnitChanged = { unit ->
                    settingsViewModel.setTemperatureUnit(unit)
                    weatherViewModel.reloadWeather()
                },

                onLanguageChanged = { lang ->

                    settingsViewModel.setLanguage(lang)

                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(lang)
                    )

                    weatherViewModel.reloadWeather()
                }
            )
        }
    }
}
