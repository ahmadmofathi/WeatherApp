package com.example.weatherapp.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
            } else {
                // GPS denied — show friendly state instead of infinite loading
                weatherViewModel.setLocationDenied()
            }
        }

    // Track if we have ever launched the permission request in this session
    var hasRequestedPermission by remember { mutableStateOf(false) }

    // Helper to request location or open app settings if permanently denied
    fun requestLocationOrOpenSettings() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            locationHelper.getCurrentLocation { lat, lon ->
                weatherViewModel.setLocation(lat, lon)
                weatherViewModel.loadWeather(lat, lon)
            }
        } else {
            val activity = context as? android.app.Activity
            val shouldShowRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    it, Manifest.permission.ACCESS_FINE_LOCATION
                )
            } ?: false

            if (shouldShowRationale || !hasRequestedPermission) {
                // System will show the permission dialog
                hasRequestedPermission = true
                locationPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } else {
                // User permanently denied — open app settings
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(intent)
            }
        }
    }

    LaunchedEffect(Unit) {
        // Check if permission is already granted
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            locationHelper.getCurrentLocation { lat, lon ->
                weatherViewModel.initializeLocation(lat, lon)
            }
        } else {
            hasRequestedPermission = true
            locationPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
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
                        scope.launch { drawerState.close() }

                        // Use the helper that handles all permission states
                        requestLocationOrOpenSettings()
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
                },

                onRequestLocation = {
                    // Use the helper that handles all permission states
                    requestLocationOrOpenSettings()
                },

                onGoToSearch = {
                    navController.navigate("search")
                },

                onGoToMap = {
                    navController.navigate("map")
                }
            )
        }
    }
}