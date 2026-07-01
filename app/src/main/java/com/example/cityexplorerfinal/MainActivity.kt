package com.example.cityexplorerfinal

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import com.example.cityexplorerfinal.api.GooglePlacesApiService
import com.example.cityexplorerfinal.engine.ChallengeGeneratorEngine
import com.example.cityexplorerfinal.local.AppDatabase
import com.example.cityexplorerfinal.model.Challenge
import com.example.cityexplorerfinal.model.Place
import com.example.cityexplorerfinal.ui.theme.CityExplorerFinalTheme
import com.example.cityexplorerfinal.ui.screens.HistoryScreen
import com.example.cityexplorerfinal.ui.screens.MapScreen
import com.example.cityexplorerfinal.ui.screens.StatisticsScreen
import com.example.cityexplorerfinal.ui.theme.screens.ChallengeDetailsScreen
import com.example.cityexplorerfinal.ui.theme.screens.MainScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "city_database")
            .allowMainThreadQueries()
            .build()

        setContent {
            CityExplorerFinalTheme {
                AppNavigation(db)
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun AppNavigation(db: AppDatabase) {
    var currentScreen by remember { mutableStateOf("MAIN") }
    var activeChallenge by remember { mutableStateOf<Challenge?>(null) }
    var historyList by remember { mutableStateOf(db.challengeDao().getAllChallenges()) }
    var nearbyPlaces by remember { mutableStateOf<List<Place>>(emptyList()) }

    // Herramientas de GPS
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }

    val apiService = remember { GooglePlacesApiService() }
    val engine = remember { ChallengeGeneratorEngine() }
    val coroutineScope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
        if (perms[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            Toast.makeText(context, "Location permission granted. Ready to generate!", Toast.LENGTH_SHORT).show()
        } else {
            locationError = "Location permission is required for the app to function."
            Toast.makeText(context, locationError, Toast.LENGTH_LONG).show()
        }
    }

    when (currentScreen) {
        "MAIN" -> {
            MainScreen(
                activeChallenge = activeChallenge,
                historyList = historyList,
                onOpenMapClick = { currentScreen = "MAP" },
                onDetailsClick = { currentScreen = "DETAILS" },
                onGenerateNewClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            if (location != null) {
                                userLocation = GeoPoint(location.latitude, location.longitude)

                                coroutineScope.launch {
                                    nearbyPlaces = apiService.fetchNearbyPlaces(location.latitude, location.longitude)
                                    val newChallenge = engine.generateChallenge(nearbyPlaces, historyList, null)

                                    if (newChallenge != null) {
                                        activeChallenge = newChallenge
                                    } else {
                                        activeChallenge = Challenge(
                                            title = "Free Exploration",
                                            description = "No specific spots matched your criteria nearby. Take a free walk and discover the unexpected!",
                                            distance = 500.0,
                                            category = "Exploration",
                                            challengeType = "Fallback",
                                            status = "ACTIVE",
                                            placeId = "fallback_${System.currentTimeMillis()}",
                                            reasoning = "Fallback scenario triggered: API failed or Engine filtered all."
                                        )
                                    }
                                }
                            } else {
                                Toast.makeText(context, "GPS data unavailable. Please set a location in Emulator settings and open Maps.", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                    }
                },
                onHistoryClick = { currentScreen = "HISTORY" },
                onStatsClick = { currentScreen = "STATS" }
            )
        }
        "MAP" -> {
            activeChallenge?.let { challenge ->
                // Buscamos las coordenadas del lugar en la lista de la API
                val targetPlace = nearbyPlaces.find { it.id == challenge.placeId }

                // Le pasamos las ubicaciones dinámicas a la pantalla del mapa
                MapScreen(
                    activeChallenge = challenge,
                    userLocation = userLocation,
                    targetPlace = targetPlace,
                    onBackClick = { currentScreen = "MAIN" },
                    onChallengeCompleted = {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        val completedChallenge = challenge.copy(
                            status = "COMPLETED",
                            completionTime = dateFormat.format(Date())
                        )
                        db.challengeDao().insertChallenge(completedChallenge)
                        historyList = db.challengeDao().getAllChallenges()
                        activeChallenge = null
                        currentScreen = "MAIN"
                    }
                )
            }
        }
        "DETAILS" -> {
            activeChallenge?.let { challenge ->
                ChallengeDetailsScreen(
                    challenge = challenge,
                    onBackClick = { currentScreen = "MAIN" }
                )
            }
        }
        "HISTORY" -> HistoryScreen(historyList = historyList, onBackClick = { currentScreen = "MAIN" })
        "STATS" -> StatisticsScreen(historyList = historyList, onBackClick = { currentScreen = "MAIN" })
    }
}