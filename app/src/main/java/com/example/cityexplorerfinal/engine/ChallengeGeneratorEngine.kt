package com.example.cityexplorerfinal.engine

import com.example.cityexplorerfinal.model.Challenge
import com.example.cityexplorerfinal.model.Place
import java.util.Calendar

class ChallengeGeneratorEngine {

    // Memoria para guardar el último lugar sugerido y evitar que se repita al pulsar "New Challenge" (Skip)
    private var lastGeneratedPlaceId: String? = null

    fun generateChallenge(
        nearbyPlaces: List<Place>,
        history: List<Challenge>,
        categoryFilter: String?
    ): Challenge? {
        var validPlaces = nearbyPlaces

        // Filtro por categoría desde los chips de la interfaz
        if (categoryFilter != null) {
            validPlaces = validPlaces.filter { it.category == categoryFilter }
        }

        // --- 3.3 CHALLENGE GENERATION LOGIC (5 RULES) ---

        // REGLA 1: Evitar repetición usando el Historial de la Base de Datos
        val recentPlaceIds = history.takeLast(10).map { it.placeId }
        validPlaces = validPlaces.filter { it.id !in recentPlaceIds }

        // REGLA 1.5 (FIX DE INTERFAZ): Evitar que te sugiera el MISMO lugar que tienes ahora en pantalla
        if (lastGeneratedPlaceId != null) {
            validPlaces = validPlaces.filter { it.id != lastGeneratedPlaceId }
        }

        // REGLA 2: Fatiga de Categoría (Si últimamente vas a muchos sitios de comida, te obliga a cambiar)
        val recentCategories = history.takeLast(3).map { it.category }
        if (recentCategories.count { it == "Food" } >= 2 && categoryFilter == null) {
            validPlaces = validPlaces.filter { it.category != "Food" }
        }

        // REGLA 3: Restricción por Hora (Por seguridad, evita enviar a parques de noche)
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val isNightTime = currentHour < 6 || currentHour > 19
        if (isNightTime) {
            validPlaces = validPlaces.filter { it.category != "Park" }
        }

        // REGLA 4: Límite de Distancia Dinámico (Si es de noche busca cosas más cerca)
        if (isNightTime) {
            validPlaces = validPlaces.filter { it.distance <= 1000.0 }
        } else {
            validPlaces = validPlaces.filter { it.distance <= 3000.0 }
        }

        // REGLA 5: Fomentar un mínimo de exploración (Descartar cosas a menos de 50 metros)
        validPlaces = validPlaces.filter { it.distance >= 50.0 }

        // FIX: Selección ALEATORIA de la lista filtrada de la API de Google, en vez de coger siempre el más cercano
        val targetPlace = validPlaces.randomOrNull() ?: return null

        // Guardamos en memoria el ID generado para que el próximo "New Challenge" lo ignore
        lastGeneratedPlaceId = targetPlace.id

        return Challenge(
            title = "Explore: ${targetPlace.name}",
            description = targetPlace.description, // Muestra el Rating de Google Places
            distance = targetPlace.distance,
            category = targetPlace.category,
            challengeType = "Dynamic Challenge",
            status = "ACTIVE",
            placeId = targetPlace.id,
            reasoning = "Generated at $currentHour:00. Randomly selected from Google Places API, filtering out history and current active challenge."
        )
    }
}