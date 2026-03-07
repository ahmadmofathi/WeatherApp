package com.example.weatherapp.ui.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherapp.ui.weather.WeatherScreen
import com.example.weatherapp.utils.LocationHelper
import com.example.weatherapp.viewmodel.FavoritesViewModel
import com.example.weatherapp.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    favoritesViewModel: FavoritesViewModel,
    weatherViewModel: WeatherViewModel,
    navController: NavController
) {

    val context = LocalContext.current

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

    val locationHelper = remember {
        LocationHelper(context)
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {

                locationHelper.getCurrentLocation { lat, lon ->

                    weatherViewModel.loadWeather(lat, lon)

                }
            }
        }

    LaunchedEffect(Unit) {

        locationPermissionLauncher.launch(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    ModalNavigationDrawer(

        drawerState = drawerState,

        drawerContent = {

            ModalDrawerSheet(

                modifier = Modifier.width(300.dp),

                drawerContainerColor = MaterialTheme.colorScheme.surface,

                drawerTonalElevation = 8.dp

            ) {

                DrawerContent(

                    favoritesViewModel = favoritesViewModel,

                    onFavoriteClick = { lat, lon ->

                        weatherViewModel.loadWeather(lat, lon)

                        scope.launch {
                            drawerState.close()
                        }
                    },

                    onSettingsClick = {
                        navController.navigate("settings")
                    },

                    onAlertsClick = {
                        navController.navigate("alerts")
                    }
                )
            }
        }

    ) {

        WeatherScreen(

            lat = 0.0,
            lon = 0.0,

            viewModel = weatherViewModel,

            onMenuClick = {

                scope.launch {
                    drawerState.open()
                }
            }
        )
    }
}