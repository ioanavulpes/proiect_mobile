package com.localpulse.data.repository.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.localpulse.data.model.Event
import com.localpulse.data.model.GeoPoint
import com.localpulse.data.model.User
import com.localpulse.data.repository.UserBehaviorRepository
import com.localpulse.data.repository.UserBehaviorData
import com.localpulse.data.repository.EventInteraction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseUserBehaviorRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserBehaviorRepository {
    
    override suspend fun generateRecommendations(
        user: User,
        currentLocation: GeoPoint,
        limit: Int
    ): Flow<List<Event>> = flow {
        try {
            // Get user behavior data
            val behaviorData = getUserBehaviorData(user.id)
            
            // Query events based on user preferences and behavior
            var query = firestore.collection("events")
                .whereEqualTo("isActive", true)
                .limit(limit.toLong())
            
            // Apply category filter based on user preferences
            if (user.preferences.categories.isNotEmpty()) {
                query = query.whereIn("category", user.preferences.categories.map { it.name })
            }
            
            val snapshot = query.get().await()
            val events = snapshot.documents.mapNotNull { it.toObject(Event::class.java) }
            
            // TODO: Implement ML-based ranking algorithm
            // For now, return events sorted by distance
            val sortedEvents = events.sortedBy { event ->
                calculateDistance(
                    currentLocation.latitude, currentLocation.longitude,
                    event.location.latitude, event.location.longitude
                )
            }
            
            emit(sortedEvents)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    override suspend fun trackEventView(eventId: String, duration: Long) {
        try {
            val interaction = EventInteraction(
                eventId = eventId,
                action = "view",
                timestamp = System.currentTimeMillis(),
                duration = duration
            )
            
            firestore.collection("userBehavior")
                .document("interactions")
                .collection("events")
                .add(interaction)
                .await()
        } catch (e: Exception) {
            // Handle error silently
        }
    }
    
    override suspend fun trackEventInteraction(eventId: String, action: String) {
        try {
            val interaction = EventInteraction(
                eventId = eventId,
                action = action,
                timestamp = System.currentTimeMillis()
            )
            
            firestore.collection("userBehavior")
                .document("interactions")
                .collection("events")
                .add(interaction)
                .await()
        } catch (e: Exception) {
            // Handle error silently
        }
    }
    
    override suspend fun updateUserPreferences(userId: String, preferences: Map<String, Any>) {
        try {
            firestore.collection("users")
                .document(userId)
                .update(preferences)
                .await()
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    override suspend fun getUserBehaviorData(userId: String): UserBehaviorData {
        return try {
            val snapshot = firestore.collection("userBehavior")
                .document(userId)
                .get()
                .await()
            
            snapshot.toObject(UserBehaviorData::class.java) ?: UserBehaviorData()
        } catch (e: Exception) {
            UserBehaviorData()
        }
    }
    
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }
}
