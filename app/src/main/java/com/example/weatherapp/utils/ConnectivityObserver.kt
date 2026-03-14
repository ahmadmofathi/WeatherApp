package com.example.weatherapp.utils

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for network connectivity monitoring.
 * Enables testing without Android Context.
 */
interface ConnectivityObserver {
    val isConnected: StateFlow<Boolean>
}
