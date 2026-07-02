package com.example.cityexplorerfinal.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cityexplorerfinal.model.Challenge

// Incrementamos la versión a 2 para forzar a Room a aplicar la migración destructiva
@Database(entities = [Challenge::class], version = 7, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Conectamos la base de datos con tu nuevo DAO
    abstract fun challengeDao(): ChallengeDAO

}
