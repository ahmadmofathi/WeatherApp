package com.example.weatherapp.ui.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    viewModel: FavoritesViewModel
) {
    val state by viewModel.uiState.collectAsState()
    when(state){
        is FavoritesUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is FavoritesUiState.Empty -> Text(text = "No favorites")
        is FavoritesUiState.Error -> Text(text = (state as FavoritesUiState.Error).message)
        is FavoritesUiState.Success ->{
            val favorites = (state as FavoritesUiState.Success).favorites
            LazyColumn {
                items(favorites) { location ->
                    FavoriteItem(
                        location = location,
                        onDelete = {
                            viewModel.removeFavorite(location)
                        }
                    )
                }
            }
        }
    }
}