package com.example.cityexplorerfinal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.cityexplorerfinal.model.Challenge
import com.example.cityexplorerfinal.model.Place
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    activeChallenge: Challenge,
    userLocation: GeoPoint?,
    targetPlace: Place?,
    onBackClick: () -> Unit,
    onChallengeCompleted: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Map") },
                navigationIcon = {
                    Button(onClick = onBackClick, modifier = Modifier.padding(8.dp)) {
                        Text("Back")
                    }
                }
            )
        },
        bottomBar = {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Target: ${activeChallenge.title.replace("Explore: ", "")}", fontWeight = FontWeight.Bold)
                    Text("Distance: ${activeChallenge.distance} m")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onChallengeCompleted,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Check Completion")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    Configuration.getInstance().userAgentValue = context.packageName
                    MapView(context).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)

                        // Centrar dinámicamente en la ubicación real del usuario
                        if (userLocation != null) {
                            controller.setZoom(16.0)
                            controller.setCenter(userLocation)
                        }
                    }
                },
                update = { mapView ->
                    mapView.overlays.clear()

                    // 1. Añadir marcador del USUARIO (Requisito 4.2.2)
                    if (userLocation != null) {
                        val userMarker = Marker(mapView).apply {
                            position = userLocation
                            title = "You are here"
                            icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_mylocation)
                        }
                        mapView.overlays.add(userMarker)
                    }

                    // 2. Añadir marcador del DESTINO (Requisito 4.2.2)
                    if (targetPlace != null) {
                        val targetGeoPoint = GeoPoint(targetPlace.latitude, targetPlace.longitude)
                        val destinationMarker = Marker(mapView).apply {
                            position = targetGeoPoint
                            title = targetPlace.name
                            icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_compass)
                        }
                        mapView.overlays.add(destinationMarker)
                    }

                    mapView.invalidate()
                }
            )
        }
    }
}