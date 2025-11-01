package com.localpulse.data.repository

import com.localpulse.data.model.Event
import com.localpulse.data.model.GeoPoint
import com.localpulse.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserBehaviorRepository {
    suspend fun generateRecommendations(
        user: User,
        currentLocation: GeoPoint,
        limit: Int = 20
    ): Flow<List<Event>>
    
    suspend fun trackEventView(eventId: String, duration: Long)
    suspend fun trackEventInteraction(eventId: String, action: String)
    suspend fun updateUserPreferences(userId: String, preferences: Map<String, Any>)
    suspend fun getUserBehaviorData(userId: String): UserBehaviorData
}

data class UserBehaviorData(
    val viewedEvents: List<String> = emptyList(),
    val interactionHistory: List<EventInteraction> = emptyList(),
    val preferences: Map<String, Any> = emptyMap(),
    val lastUpdated: Long = 0L
)

data class EventInteraction(
    val eventId: String,
    val action: String,
    val timestamp: Long,
    val duration: Long = 0L
)
