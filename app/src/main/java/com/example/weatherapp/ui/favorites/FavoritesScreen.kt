package com.example.weatherapp.ui.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.weatherapp.viewmodel.FavoritesViewModel

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onAddClick: () -> Unit,
    onFavoriteClick: (Double, Double) -> Unit
) {

    val state by viewModel.uiState.collectAsState()

    Column {

        Button(
            onClick = onAddClick,
            Modifier.fillMaxWidth()
        ) {
            Text("Add Favorite")
        }

        when (state) {

            is FavoritesUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is FavoritesUiState.Empty -> {
                Text("No favorites")
            }

            is FavoritesUiState.Error -> {
                Text((state as FavoritesUiState.Error).message)
            }

            is FavoritesUiState.Success -> {

                val favorites =
                    (state as FavoritesUiState.Success).favorites

                LazyColumn {

                    items(favorites) { location ->

                        FavoriteItem(
                            location = location,
                            onClick = {
                                onFavoriteClick(
                                    location.latitude,
                                    location.longitude
                                )
                            },
                            onDelete = {
                                viewModel.removeFavorite(location)
                            }
                        )
                    }
                }
            }
        }
    }
}