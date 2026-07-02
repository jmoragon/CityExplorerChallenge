package com.example.cityexplorerfinal.engine

import com.example.cityexplorerfinal.model.Challenge
import com.example.cityexplorerfinal.model.Place
import java.util.Calendar

class ChallengeGeneratorEngine {

    private var lastGeneratedPlaceId: String? = null

    fun generateChallenge(
        nearbyPlaces: List<Place>,
        history: List<Challenge>,
        categoryFilter: String?
    ): Challenge? {
        var validPlaces = nearbyPlaces

        // Constructor del texto "Reason:" dinámico
        val reasoningBuilder = StringBuilder()

        if (categoryFilter != null) {
            validPlaces = validPlaces.filter { it.category == categoryFilter }
            reasoningBuilder.append("- User explicitly requested ${categoryFilter} category\n")
        }

        // REGLA 1 & 1.5: Evitar repetición
        val recentPlaceIds = history.takeLast(10).map { it.placeId }
        validPlaces = validPlaces.filter { it.id !in recentPlaceIds && it.id != lastGeneratedPlaceId }
        reasoningBuilder.append("- Place not visited in recent history\n")

        // REGLA 2: Fatiga de Categoría
        val recentCategories = history.takeLast(3).map { it.category }
        if (recentCategories.count { it == "Food" } >= 2 && categoryFilter == null) {
            validPlaces = validPlaces.filter { it.category != "Food" }
            reasoningBuilder.append("- Category fatigue: Avoided food places to encourage variety\n")
        }

        // REGLA 3 y 4: Hora y Distancia
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val isNightTime = currentHour < 6 || currentHour > 19

        if (isNightTime) {
            validPlaces = validPlaces.filter { it.category != "Park" }
            validPlaces = validPlaces.filter { it.distance <= 1000.0 }
            reasoningBuilder.append("- Nighttime protocol: Avoided parks for safety\n")
            reasoningBuilder.append("- Nighttime protocol: Distance limited to 1000m\n")
        } else {
            validPlaces = validPlaces.filter { it.distance <= 3000.0 }
        }

        // REGLA 5: Distancia mínima
        validPlaces = validPlaces.filter { it.distance >= 50.0 }

        val targetPlace = validPlaces.randomOrNull() ?: return null

        lastGeneratedPlaceId = targetPlace.id

        // Añadimos las razones finales específicas del lugar seleccionado
        reasoningBuilder.append("- Nearby ${targetPlace.category.lowercase()} detected\n")
        reasoningBuilder.append("- Distance below ${if (isNightTime) 1000 else 3000}m (Actual: ${targetPlace.distance}m)")

        return Challenge(
            title = "Explore: ${targetPlace.name}",
            description = targetPlace.description,
            distance = targetPlace.distance,
            category = targetPlace.category,
            challengeType = "Dynamic Challenge",
            status = "ACTIVE",
            placeId = targetPlace.id,
            reasoning = reasoningBuilder.toString(),
            photoUrl = targetPlace.photoUrl // Pasamos la URL de la foto al reto
        )
    }
}
