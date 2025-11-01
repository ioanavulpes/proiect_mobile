package com.localpulse.data.repository

import com.localpulse.data.model.Badge
import com.localpulse.data.model.UserProfile
import com.localpulse.domain.usecase.PointAction

interface GamificationRepository {
    suspend fun awardPoints(
        userId: String,
        action: PointAction,
        eventId: String? = null
    ): Int
    
    suspend fun getUserProfile(userId: String): UserProfile
    suspend fun getLeaderboard(city: String, limit: Int = 50): List<LeaderboardEntry>
    suspend fun checkAchievements(userId: String): List<Badge>
    suspend fun updateStreak(userId: String): Int
}

data class LeaderboardEntry(
    val userId: String,
    val displayName: String,
    val photoUrl: String,
    val points: Int,
    val level: Int,
    val rank: Int
)
