package com.example.weatherapp.ui.map

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
import androidx.compose.ui.viewinterop.AndroidView
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

    Column {

        AndroidView(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            factory = { context ->

                val map = MapView(context).apply {

                    setTileSource(TileSourceFactory.MAPNIK)

                    setMultiTouchControls(true)

                    setUseDataConnection(true)

                    val startPoint = GeoPoint(30.0444, 31.2357)

                    controller.setZoom(10.0)

                    controller.setCenter(startPoint)
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
            Text("Select Location")
        }
    }
}