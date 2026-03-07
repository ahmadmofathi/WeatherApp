package com.example.weatherapp.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.favorites.FavoritesUiState
import com.example.weatherapp.viewmodel.FavoritesViewModel

@Composable
fun DrawerContent(

    favoritesViewModel: FavoritesViewModel,

    onFavoriteClick: (Double, Double) -> Unit,

    onSettingsClick: () -> Unit,

    onAlertsClick: () -> Unit

) {

    val state by favoritesViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Weather App",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        Divider()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Favorites",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (state is FavoritesUiState.Success) {

            val favorites =
                (state as FavoritesUiState.Success).favorites

            LazyColumn {

                items(favorites) { location ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                                onFavoriteClick(
                                    location.latitude,
                                    location.longitude
                                )
                            }
                            .padding(vertical = 12.dp),
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
            text = "⚠ Alerts",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAlertsClick() }
                .padding(vertical = 12.dp)
        )

        Text(
            text = "⚙ Settings",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSettingsClick() }
                .padding(vertical = 12.dp)
        )
    }
}