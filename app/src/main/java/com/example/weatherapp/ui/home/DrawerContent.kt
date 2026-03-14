package com.example.weatherapp.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
            .padding(24.dp)
    ) {

        // App title
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // My Location
        DrawerMenuItem(
            icon = Icons.Outlined.MyLocation,
            label = stringResource(R.string.my_location),
            onClick = onMyLocationClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Favorites section
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.favorites),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        if (state is FavoritesUiState.Success) {

            val favorites = (state as FavoritesUiState.Success).favorites

            if (favorites.isEmpty()) {
                Text(
                    text = "No favorite locations yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 26.dp, top = 4.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(favorites) { location ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
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
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = location.cityName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom nav items
        DrawerMenuItem(
            icon = Icons.Outlined.Notifications,
            label = stringResource(R.string.alerts),
            onClick = onAlertsClick
        )

        DrawerMenuItem(
            icon = Icons.Outlined.Settings,
            label = stringResource(R.string.settings),
            onClick = onSettingsClick
        )
    }

    if (showDialog) {

        AlertDialog(

            onDismissRequest = { showDialog = false },

            title = {
                Text(
                    text = stringResource(R.string.remove_city),
                    style = MaterialTheme.typography.titleMedium
                )
            },

            text = {
                Text(
                    text = stringResource(R.string.remove_city_confirmation),
                    style = MaterialTheme.typography.bodyMedium
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        selectedLocation?.let {
                            favoritesViewModel.removeFavorite(it)
                        }
                        showDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
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

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}