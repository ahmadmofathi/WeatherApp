package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.navigation.Screen
import com.example.weatherapp.ui.favorites.FavoritesScreen
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.viewmodel.FavoritesViewModel
import com.example.weatherapp.viewmodel.FavoritesViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WeatherAppTheme {
                WeatherApp(context = this)
            }
        }
    }
}

@Composable
fun WeatherApp(context: Context) {

    val navController = rememberNavController()

    val database = remember {
        Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_db"
        ).build()
    }

    val repository = remember {
        WeatherRepositoryImpl(database.favoriteDao())
    }

    val viewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(repository)
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Favorites.route
    ) {

        composable(Screen.Favorites.route) {
            FavoritesScreen(viewModel = viewModel)
        }
    }
}