package com.example.cityexplorerfinal.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cityexplorerfinal.model.Challenge

@Dao
interface ChallengeDAO {

    // CORRECCIÓN: Ahora busca en la tabla correcta "challenges"
    @Query("SELECT * FROM challenges")
    fun getAllChallenges(): List<Challenge>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChallenge(challenge: Challenge)

    // CORRECCIÓN: Ahora borra de la tabla correcta "challenges" (si usas esta función)
    @Query("DELETE FROM challenges")
    fun deleteAllChallenges()
}
