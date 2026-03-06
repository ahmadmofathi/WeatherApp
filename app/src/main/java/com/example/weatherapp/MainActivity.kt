package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.remote.RetrofitInstance
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.navigation.AppNavigation
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.viewmodel.FavoritesViewModel
import com.example.weatherapp.viewmodel.FavoritesViewModelFactory
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.example.weatherapp.viewmodel.WeatherViewModelFactory
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // OSMDroid configuration
        Configuration.getInstance().load(
            applicationContext,
            applicationContext.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )

        setContent {
            WeatherAppTheme {
                WeatherApp(context = this)
            }
        }
    }
}

@Composable
fun WeatherApp(context: Context) {

    val database = remember {
        Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_db"
        ).build()
    }

    val api = remember {
        RetrofitInstance.api
    }

    val repository = remember {
        WeatherRepositoryImpl(
            database.favoriteDao(),
            api
        )
    }

    val favoritesViewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(repository)
    )
    val weatherViewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(repository)
    )

    AppNavigation(
        favoritesViewModel = favoritesViewModel,
        weatherViewModel = weatherViewModel
    )

}