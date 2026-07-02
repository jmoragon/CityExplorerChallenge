package com.example.cityexplorerfinal.api

import com.example.cityexplorerfinal.model.Place
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.math.*

class GooglePlacesApiService {
    private val client = OkHttpClient()
    // REQUISITO 5.2: Recuerda borrar esta clave antes de hacer el "git push" a tu repositorio
    private val googleApiKey = "INTRODUCE_YOUR_GOOGLE_PLACES_API_CODE"

    // 3.2 External Data Sources: Fetching real places using ONLY Google Places API
    suspend fun fetchNearbyPlaces(lat: Double, lon: Double): List<Place> = withContext(Dispatchers.IO) {
        val places = mutableListOf<Place>()

        // Eliminamos el 'if' que bloqueaba la ejecución. ¡Ahora sí hará la petición a Google!

        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$lat%2C$lon&radius=2000&type=point_of_interest&key=$googleApiKey"
        val request = Request.Builder().url(url).build()

        try {
            Log.d("CityExplorer", "Llamando a Google API con Lat: $lat, Lon: $lon")

            val response = client.newCall(request).execute()
            val responseData = response.body?.string()

            // ¡ESTA LÍNEA ES LA CLAVE! Imprimirá lo que Google nos está respondiendo realmente
            Log.d("CityExplorer", "Respuesta de Google: $responseData")

            if (responseData != null) {
                val json = JSONObject(responseData)
                if (json.has("results")) {
                    val results = json.getJSONArray("results")
                    Log.d("CityExplorer", "Lugares encontrados en el JSON: ${results.length()}")

                    // ... (Aquí sigue tu bucle for original para parsear los lugares) ...
                    for (i in 0 until results.length()) {
                        val item = results.getJSONObject(i)
                        val name = item.optString("name", "Unknown Place")
                        val id = item.optString("place_id", i.toString())

                        val geometry = item.getJSONObject("geometry").getJSONObject("location")
                        val pLat = geometry.getDouble("lat")
                        val pLon = geometry.getDouble("lng")

                        var category = "Social"
                        if (item.has("types")) {
                            val typesString = item.getJSONArray("types").toString()
                            category = when {
                                typesString.contains("cafe") || typesString.contains("restaurant") -> "Food"
                                typesString.contains("museum") || typesString.contains("art_gallery") -> "Culture"
                                typesString.contains("park") || typesString.contains("tourist_attraction") -> "Park"
                                else -> "Social"
                            }
                        }

                        val rating = item.optDouble("rating", 0.0)
                        val distance = calculateDistance(lat, lon, pLat, pLon)

                        // NUEVO: Obtener la foto de Google Places
                        var photoUrl: String? = null
                        if (item.has("photos")) {
                            val photos = item.getJSONArray("photos")
                            if (photos.length() > 0) {
                                val photoRef = photos.getJSONObject(0).getString("photo_reference")
                                photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photo_reference=$photoRef&key=$googleApiKey"
                            }
                        }

                        places.add(Place(id, name, "Google Rating: $rating/5.0", pLat, pLon, category, kotlin.math.round(distance), photoUrl))
                    }
                } else if (json.has("error_message")) {
                    // Si Google nos da un error de permisos o facturación
                    Log.e("CityExplorer", "Error de Google API: ${json.getString("error_message")}")
                }
            }
        } catch (e: Exception) {
            Log.e("CityExplorer", "Excepción de Red: ${e.message}")
            e.printStackTrace()
        }

        Log.d("CityExplorer", "Total de lugares devueltos por el Service: ${places.size}")
        return@withContext places
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371e3
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
