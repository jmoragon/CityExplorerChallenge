package com.example.cityexplorerfinal.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenge_history")
data class Challenge(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val distance: Double,
    val category: String,
    val challengeType: String,
    val status: String, // "ACTIVE", "COMPLETED", "EXPIRED"
    val placeId: String,
    val reasoning: String, // La explicación para la pantalla de Detalles
    val completionTime: String? = null // Fecha en la que se completó
)