package com.example.weatherapp.utils

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices

class LocationHelper(
    context: Context
) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        onLocationResult: (Double, Double) -> Unit
    ) {

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->

                location?.let {

                    onLocationResult(
                        it.latitude,
                        it.longitude
                    )
                }
            }
    }
}