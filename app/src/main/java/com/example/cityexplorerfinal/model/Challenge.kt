package com.example.cityexplorerfinal.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenges")
data class Challenge(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val distance: Double,
    val category: String,
    val challengeType: String,
    val status: String,
    val placeId: String,
    val completionTime: String? = null,
    val reasoning: String? = null,
    val photoUrl: String? = null
)
