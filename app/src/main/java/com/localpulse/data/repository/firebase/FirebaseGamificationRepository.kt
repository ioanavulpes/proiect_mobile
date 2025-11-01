package com.localpulse.data.repository.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.localpulse.data.model.Badge
import com.localpulse.data.model.UserProfile
import com.localpulse.data.repository.GamificationRepository
import com.localpulse.data.repository.LeaderboardEntry
import com.localpulse.domain.usecase.PointAction
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseGamificationRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : GamificationRepository {
    
    override suspend fun awardPoints(
        userId: String,
        action: PointAction,
        eventId: String?
    ): Int {
        val points = when (action) {
            PointAction.EVENT_CHECK_IN -> 10
            PointAction.EVENT_CREATION -> 25
            PointAction.CHAT_MESSAGE -> 2
            PointAction.PROFILE_COMPLETION -> 15
            PointAction.FIRST_EVENT -> 50
            PointAction.WEEKLY_STREAK -> 30
            PointAction.MONTHLY_ACTIVE -> 100
        }
        
        try {
            val userRef = firestore.collection("users").document(userId)
            val userDoc = userRef.get().await()
            
            if (userDoc.exists()) {
                val currentProfile = userDoc.toObject(UserProfile::class.java) ?: UserProfile()
                val newPoints = currentProfile.points + points
                val newLevel = calculateLevel(newPoints)
                
                val updatedProfile = currentProfile.copy(
                    points = newPoints,
                    level = newLevel
                )
                
                userRef.update("profile", updatedProfile).await()
                
                // Check for new achievements
                checkAchievements(userId, updatedProfile)
                
                return points
            }
        } catch (e: Exception) {
            // Handle error
        }
        
        return 0
    }
    
    override suspend fun getUserProfile(userId: String): UserProfile {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            
            snapshot.toObject(UserProfile::class.java) ?: UserProfile()
        } catch (e: Exception) {
            UserProfile()
        }
    }
    
    override suspend fun getLeaderboard(city: String, limit: Int): List<LeaderboardEntry> {
        return try {
            val snapshot = firestore.collection("users")
                .whereEqualTo("city", city)
                .orderBy("profile.points", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            
            snapshot.documents.mapIndexed { index, document ->
                val user = document.toObject(com.localpulse.data.model.User::class.java)
                val profile = user?.profile ?: UserProfile()
                
                LeaderboardEntry(
                    userId = document.id,
                    displayName = user?.displayName ?: "Unknown",
                    photoUrl = user?.photoUrl ?: "",
                    points = profile.points,
                    level = profile.level,
                    rank = index + 1
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun checkAchievements(userId: String): List<Badge> {
        val profile = getUserProfile(userId)
        val newBadges = mutableListOf<Badge>()
        
        // Check for various achievements
        if (profile.eventsAttended >= 1 && !hasBadge(profile.badges, "first_event")) {
            newBadges.add(createBadge("first_event", "First Event", "Attended your first event"))
        }
        
        if (profile.eventsAttended >= 10 && !hasBadge(profile.badges, "event_explorer")) {
            newBadges.add(createBadge("event_explorer", "Event Explorer", "Attended 10 events"))
        }
        
        if (profile.points >= 100 && !hasBadge(profile.badges, "point_collector")) {
            newBadges.add(createBadge("point_collector", "Point Collector", "Earned 100 points"))
        }
        
        if (profile.streak >= 7 && !hasBadge(profile.badges, "week_warrior")) {
            newBadges.add(createBadge("week_warrior", "Week Warrior", "7-day activity streak"))
        }
        
        // Award new badges
        if (newBadges.isNotEmpty()) {
            val updatedBadges = profile.badges + newBadges
            val updatedProfile = profile.copy(badges = updatedBadges)
            
            firestore.collection("users")
                .document(userId)
                .update("profile", updatedProfile)
                .await()
        }
        
        return newBadges
    }
    
    override suspend fun updateStreak(userId: String): Int {
        val profile = getUserProfile(userId)
        val newStreak = profile.streak + 1
        
        val updatedProfile = profile.copy(streak = newStreak)
        
        firestore.collection("users")
            .document(userId)
            .update("profile", updatedProfile)
            .await()
        
        return newStreak
    }
    
    private fun calculateLevel(points: Int): Int {
        return when {
            points >= 1000 -> 10
            points >= 500 -> 9
            points >= 250 -> 8
            points >= 150 -> 7
            points >= 100 -> 6
            points >= 75 -> 5
            points >= 50 -> 4
            points >= 25 -> 3
            points >= 10 -> 2
            else -> 1
        }
    }
    
    private fun hasBadge(badges: List<Badge>, badgeId: String): Boolean {
        return badges.any { it.id == badgeId }
    }
    
    private fun createBadge(id: String, name: String, description: String): Badge {
        return Badge(
            id = id,
            name = name,
            description = description,
            earnedAt = java.util.Date()
        )
    }
}
