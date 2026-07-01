package com.example.cityexplorerfinal.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cityexplorerfinal.model.Challenge

@Dao
interface ChallengeDao {
    @Insert
    fun insertChallenge(challenge: Challenge)

    @Query("SELECT * FROM challenge_history ORDER BY id DESC")
    fun getAllChallenges(): List<Challenge>

    @Query("SELECT * FROM challenge_history WHERE status = 'ACTIVE' LIMIT 1")
    fun getActiveChallenge(): Challenge?

    @Query("UPDATE challenge_history SET status = 'COMPLETED', completionTime = :time WHERE id = :challengeId")
    fun markChallengeAsCompleted(challengeId: Int, time: String)
}