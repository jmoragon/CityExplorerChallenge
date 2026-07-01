package com.example.cityexplorerfinal.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cityexplorerfinal.model.Challenge

@Database(entities = [Challenge::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun challengeDao(): ChallengeDao
}