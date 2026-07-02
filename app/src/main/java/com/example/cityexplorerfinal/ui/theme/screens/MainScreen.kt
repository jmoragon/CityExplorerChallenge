package com.example.cityexplorerfinal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.cityexplorerfinal.R
import com.example.cityexplorerfinal.model.Challenge
import com.example.cityexplorerfinal.model.Place
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MainScreen(
    activeChallenge: Challenge?,
    historyList: List<Challenge>,
    nearbyPlaces: List<Place>,
    userLocation: GeoPoint?,
    onOpenMapClick: () -> Unit,
    onDetailsClick: () -> Unit,
    onGenerateNewClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onStatsClick: () -> Unit
) {
    val context = LocalContext.current
    val totalCompleted = historyList.size
    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val completedToday = historyList.count { it.completionTime?.startsWith(todayStr) == true }

    Box(modifier = Modifier.fillMaxSize()) {
        // FONDO: Mapa Inmersivo Kairos con Pines de Colores
        if (userLocation != null) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    Configuration.getInstance().userAgentValue = context.packageName
                    MapView(context).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)
                        controller.setZoom(15.5)
                        controller.setCenter(userLocation)
                    }
                },
                update = { mapView ->
                    mapView.overlays.clear()

                    // Pin Usuario
                    val uMarker = Marker(mapView).apply {
                        position = userLocation
                        title = "You are here"
                        icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_mylocation)
                    }
                    mapView.overlays.add(uMarker)

                    // Pines de Colores para cada Categoría
                    nearbyPlaces.forEach { place ->
                        val marker = Marker(mapView).apply {
                            position = GeoPoint(place.latitude, place.longitude)
                            title = place.name
                            val customIcon = ContextCompat.getDrawable(context, R.drawable.ic_pin)?.mutate()
                            val tintColor = when (place.category) {
                                "Food" -> android.graphics.Color.parseColor("#FFC107") // Amarillo
                                "Culture" -> android.graphics.Color.parseColor("#E91E63") // Rosa
                                "Park" -> android.graphics.Color.parseColor("#4CAF50") // Verde
                                else -> android.graphics.Color.parseColor("#2196F3") // Azul
                            }
                            if (customIcon != null) {
                                androidx.core.graphics.drawable.DrawableCompat.setTint(customIcon, tintColor)
                                icon = customIcon
                            } else {
                                icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_compass)
                            }
                        }
                        mapView.overlays.add(marker)
                    }
                    mapView.invalidate()
                }
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFEFEFEF)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        // CAPA SUPERIOR: Interfaz que cumple el PDF 4.2.1
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Kairos
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp).clip(RoundedCornerShape(24.dp)).background(Color.White.copy(alpha = 0.9f)).padding(16.dp)
            ) {
                Image(painter = painterResource(id = R.drawable.kairos_logo), contentDescription = "Kairos", modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("City Explorer Challenge", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Progress y Desafío Activo (Requisito 4.2.1)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Progress Card
                Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Progress", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        Text("Completed today: $completedToday", color = Color.Gray)
                        Text("Total completed: $totalCompleted", color = Color.Gray)
                    }
                }

                if (activeChallenge != null) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Active Challenge", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(activeChallenge.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(activeChallenge.description, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Category: ${activeChallenge.category}", fontWeight = FontWeight.SemiBold)
                            Text("Distance: ${activeChallenge.distance} m")
                            Text("Status: ${activeChallenge.status}")

                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Button(onClick = onOpenMapClick, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA2C2E1))) { Text("Open Map", color = Color.DarkGray) }
                                Button(onClick = onDetailsClick, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB5EAD7))) { Text("Details", color = Color.DarkGray) }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(onClick = onGenerateNewClick, modifier = Modifier.fillMaxWidth()) { Text("New Challenge", color = Color.DarkGray) }
                        }
                    }
                } else {
                    Button(onClick = onGenerateNewClick, modifier = Modifier.fillMaxWidth().height(60.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB5EAD7))) {
                        Text("Generate Challenge", color = Color.DarkGray, fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleMedium.fontSize)
                    }
                }

                // Navegación Inferior
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White.copy(alpha = 0.9f)).padding(4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    TextButton(onClick = onHistoryClick) { Text("History", color = Color.DarkGray, fontWeight = FontWeight.Bold) }
                    TextButton(onClick = onStatsClick) { Text("Statistics", color = Color.DarkGray, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}
