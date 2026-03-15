package com.example.weatherapp

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.WeatherLocalDataSourceImpl
import com.example.weatherapp.data.preferences.SettingsDataStore
import com.example.weatherapp.data.remote.RetrofitInstance
import com.example.weatherapp.data.remote.WeatherRemoteDataSourceImpl
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

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted
            } else {
                // Permission denied
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsDataStore = SettingsDataStore(this)

        // Initialize OSMDroid
        Configuration.getInstance().load(
            applicationContext,
            applicationContext.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )

        createNotificationChannel()
        checkAndRequestPermissions()

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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Weather Alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("weather_alerts", name, importance).apply {
                description = "Notifications for weather alarms and alerts"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun checkAndRequestPermissions() {
        // 1. Notification Permission (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // 2. Exact Alarm Permission (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }

        // 3. Overlay Permission (for the alarm screen to pop up)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
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

    val remoteDataSource = remember {
        WeatherRemoteDataSourceImpl(api)
    }

    val localDataSource = remember {
        WeatherLocalDataSourceImpl(
            database.favoriteDao(),
            database.cachedWeatherDao()
        )
    }

    val repository = remember {
        WeatherRepositoryImpl(
            localDataSource,
            remoteDataSource
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
