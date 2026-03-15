package com.example.weatherapp.ui.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.weatherapp.R
import com.google.android.gms.location.LocationServices
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(
    onLocationSelected: (Double, Double) -> Unit
) {

    var selectedPoint by remember {
        mutableStateOf<GeoPoint?>(null)
    }

    val context = LocalContext.current

    Column {

        AndroidView(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            factory = { ctx ->

                val map = MapView(ctx).apply {

                    setTileSource(TileSourceFactory.MAPNIK)

                    setMultiTouchControls(true)

                    setUseDataConnection(true)

                    // Default position (Cairo) — will be overridden if GPS is available
                    val defaultPoint = GeoPoint(30.0444, 31.2357)

                    controller.setZoom(10.0)

                    controller.setCenter(defaultPoint)
                }

                val marker = Marker(map).apply {

                    setAnchor(
                        Marker.ANCHOR_CENTER,
                        Marker.ANCHOR_BOTTOM
                    )
                }

                map.overlays.add(marker)

                map.overlays.add(
                    MapEventsOverlay(
                        object : MapEventsReceiver {

                            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {

                                p?.let {

                                    selectedPoint = it

                                    marker.position = it

                                    map.invalidate()
                                }

                                return true
                            }

                            override fun longPressHelper(p: GeoPoint?): Boolean = false
                        }
                    )
                )

                // Center on user's current location if permission is granted
                val hasPermission = ContextCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    try {
                        val fusedClient = LocationServices.getFusedLocationProviderClient(ctx)
                        fusedClient.lastLocation.addOnSuccessListener { location ->
                            location?.let {
                                val userPoint = GeoPoint(it.latitude, it.longitude)
                                map.controller.animateTo(userPoint)
                                map.controller.setZoom(14.0)
                            }
                        }
                    } catch (_: Exception) {
                        // Silently fall back to default position
                    }
                }

                map
            }
        )

        Button(
            onClick = {
                selectedPoint?.let {
                    onLocationSelected(it.latitude, it.longitude)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.select_from_map))
        }
    }
}