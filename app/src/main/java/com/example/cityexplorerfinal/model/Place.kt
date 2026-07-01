package com.example.cityexplorerfinal.model

data class Place(
    val id: String,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val distance: Double
)