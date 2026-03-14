package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.preferences.SettingsDataStore
import com.example.weatherapp.data.remote.RetrofitInstance
import com.example.weatherapp.data.repository.AlertRepository
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.navigation.AppNavigation
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.utils.NetworkMonitor
import com.example.weatherapp.viewmodel.AlertViewModel
import com.example.weatherapp.viewmodel.AlertViewModelFactory
import com.example.weatherapp.viewmodel.FavoritesViewModel
import com.example.weatherapp.viewmodel.FavoritesViewModelFactory
import com.example.weatherapp.viewmodel.SearchViewModel
import com.example.weatherapp.viewmodel.SearchViewModelFactory
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.example.weatherapp.viewmodel.WeatherViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsDataStore = SettingsDataStore(this)

        // Initialize OSMDroid
        Configuration.getInstance().load(
            applicationContext,
            applicationContext.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )

        // Sync DataStore locale with AppCompatDelegate once on startup
        lifecycleScope.launch {
            val savedLanguage = settingsDataStore.language.first()
            val currentLocales = AppCompatDelegate.getApplicationLocales()
            
            if (currentLocales.isEmpty || currentLocales.toLanguageTags() != savedLanguage) {
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(savedLanguage)
                )
            }
        }

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
        ).fallbackToDestructiveMigration().build()
    }

    val api = remember { RetrofitInstance.api }

    val repository = remember {
        WeatherRepositoryImpl(
            database.favoriteDao(),
            api,
            database.cachedWeatherDao()
        )
    }
    val alertRepository = remember {
        AlertRepository(database.alertDao())
    }
    
    val settingsDataStore = remember { SettingsDataStore(context) }

    val networkMonitor = remember { NetworkMonitor(context) }

    val favoritesViewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(repository)
    )
    val weatherViewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(repository, settingsDataStore, networkMonitor)
    )
    val searchViewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(repository)
    )
    val alertViewModel: AlertViewModel = viewModel(
        factory = AlertViewModelFactory(alertRepository)
    )

    AppNavigation(
        favoritesViewModel = favoritesViewModel,
        weatherViewModel = weatherViewModel,
        searchViewModel = searchViewModel,
        alertViewModel = alertViewModel
    )
}
