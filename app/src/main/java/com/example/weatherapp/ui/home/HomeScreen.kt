package com.example.weatherapp.ui.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.ui.weather.WeatherScreen
import com.example.weatherapp.utils.LocationHelper
import com.example.weatherapp.viewmodel.FavoriteEvent
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

    val snackbarHostState = remember { SnackbarHostState() }

    val isFavorite by favoritesViewModel.isFavorite.collectAsState()
    val favoriteEvent by favoritesViewModel.favoriteEvent.collectAsState()
    val weatherLocation by weatherViewModel.location.collectAsState()

    val addedText = stringResource(R.string.favorite_added)
    val removedText = stringResource(R.string.favorite_removed)

    // Check favorite status when location changes
    LaunchedEffect(weatherLocation, favoritesViewModel.uiState.collectAsState().value) {
        weatherLocation?.let { (lat, lon) ->
            favoritesViewModel.checkIfFavorite(lat, lon)
        }
    }

    // Show snackbar on favorite event
    LaunchedEffect(favoriteEvent) {
        favoriteEvent?.let { event ->
            val message = when (event) {
                FavoriteEvent.ADDED -> addedText
                FavoriteEvent.REMOVED -> removedText
            }
            snackbarHostState.showSnackbar(message)
            favoritesViewModel.clearFavoriteEvent()
        }
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {

                locationHelper.getCurrentLocation { lat, lon ->
                    weatherViewModel.initializeLocation(lat, lon)
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

                        weatherViewModel.setLocation(lat, lon)

                        weatherViewModel.loadWeather(lat, lon)

                        scope.launch { drawerState.close() }
                    },

                    onMyLocationClick = {

                        locationHelper.getCurrentLocation { lat, lon ->

                            weatherViewModel.setLocation(lat, lon)

                            weatherViewModel.loadWeather(lat, lon)
                        }

                        scope.launch { drawerState.close() }
                    },

                    onSettingsClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("settings")
                    },

                    onAlertsClick = {
                        navController.navigate("alerts")
                    }
                )
            }
        }

    ) {

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { padding ->

            WeatherScreen(

                viewModel = weatherViewModel,

                isFavorite = isFavorite,

                onMenuClick = {
                    scope.launch { drawerState.open() }
                },

                onSearchClick = {
                    navController.navigate("search")
                },

                onToggleFavoriteClick = {
                    val location = weatherViewModel.getLastLocation()
                    favoritesViewModel.toggleFavorite(
                        location.first,
                        location.second
                    )
                }
            )
        }
    }
}