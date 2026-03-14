package com.example.weatherapp.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.data.local.favorite.FavoriteLocation
import com.example.weatherapp.ui.favorites.FavoritesUiState
import com.example.weatherapp.viewmodel.FavoritesViewModel

@Composable
fun DrawerContent(

    favoritesViewModel: FavoritesViewModel,

    onFavoriteClick: (Double, Double) -> Unit,

    onMyLocationClick: () -> Unit,

    onSettingsClick: () -> Unit,

    onAlertsClick: () -> Unit
) {

    val state by favoritesViewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<FavoriteLocation?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        Divider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onMyLocationClick() }
                .padding(vertical = 12.dp)
        ) {

            Text("📍", modifier = Modifier.padding(end = 8.dp))

            Text(
                text = stringResource(R.string.my_location),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Divider()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.favorites),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (state is FavoritesUiState.Success) {

            val favorites = (state as FavoritesUiState.Success).favorites

            LazyColumn {

                items(favorites) { location ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(

                                onClick = {

                                    onFavoriteClick(
                                        location.latitude,
                                        location.longitude
                                    )
                                },

                                onLongClick = {

                                    selectedLocation = location
                                    showDialog = true
                                }

                            )
                            .padding(vertical = 12.dp)
                    ) {

                        Text(
                            text = "📍",
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        Text(
                            text = location.cityName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Divider()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.alerts),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAlertsClick() }
                .padding(vertical = 12.dp)
        )

        Text(
            text = stringResource(R.string.settings),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSettingsClick() }
                .padding(vertical = 12.dp)
        )
    }

    if (showDialog) {

        AlertDialog(

            onDismissRequest = { showDialog = false },

            title = { Text(stringResource(R.string.remove_city)) },

            text = { Text(stringResource(R.string.remove_city_confirmation)) },

            confirmButton = {

                TextButton(

                    onClick = {

                        selectedLocation?.let {
                            favoritesViewModel.removeFavorite(it)
                        }

                        showDialog = false
                    }
                ) {

                    Text(stringResource(R.string.delete))

                }
            },

            dismissButton = {

                TextButton(
                    onClick = { showDialog = false }
                ) {

                    Text(stringResource(R.string.cancel))

                }
            }
        )
    }
}