package com.localpulse.data.repository.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint as FirestoreGeoPoint
import com.localpulse.data.model.Event
import com.localpulse.data.model.EventCategory
import com.localpulse.data.model.GeoPoint
import com.localpulse.data.repository.EventRepository
import com.localpulse.data.repository.TimeRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseEventRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : EventRepository {
    
    override suspend fun getNearbyEvents(
        location: GeoPoint,
        radiusKm: Double,
        categories: List<EventCategory>,
        maxPrice: Double?,
        timeRange: TimeRange?
    ): Flow<List<Event>> = flow {
        try {
            val events = mutableListOf<Event>()
            
            // Query events from Firestore
            var query = firestore.collection("events")
                .whereEqualTo("isActive", true)
            
            // Apply category filter if specified
            if (categories.isNotEmpty()) {
                query = query.whereIn("category", categories.map { it.name })
            }
            
            // Apply price filter if specified
            if (maxPrice != null) {
                query = query.whereLessThanOrEqualTo("price", maxPrice)
            }
            
            // Apply time range filter if specified
            if (timeRange != null) {
                query = query.whereGreaterThanOrEqualTo("startDate", timeRange.start)
                    .whereLessThanOrEqualTo("startDate", timeRange.end)
            }
            
            val snapshot = query.get().await()
            
            for (document in snapshot.documents) {
                val event = document.toObject(Event::class.java)
                if (event != null) {
                    // Calculate distance and filter by radius
                    val distance = calculateDistance(
                        location.latitude, location.longitude,
                        event.location.latitude, event.location.longitude
                    )
                    
                    if (distance <= radiusKm) {
                        events.add(event)
                    }
                }
            }
            
            emit(events.sortedBy { 
                calculateDistance(
                    location.latitude, location.longitude,
                    it.location.latitude, it.location.longitude
                )
            })
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    override suspend fun getEventById(eventId: String): Event? {
        return try {
            val document = firestore.collection("events").document(eventId).get().await()
            document.toObject(Event::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun searchEvents(query: String, location: GeoPoint): Flow<List<Event>> = flow {
        try {
            val events = mutableListOf<Event>()
            val snapshot = firestore.collection("events")
                .whereEqualTo("isActive", true)
                .get().await()
            
            for (document in snapshot.documents) {
                val event = document.toObject(Event::class.java)
                if (event != null && 
                    (event.title.contains(query, ignoreCase = true) ||
                     event.description.contains(query, ignoreCase = true) ||
                     event.tags.any { it.contains(query, ignoreCase = true) })) {
                    events.add(event)
                }
            }
            
            emit(events.sortedBy {
                calculateDistance(
                    location.latitude, location.longitude,
                    it.location.latitude, it.location.longitude
                )
            })
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    override suspend fun syncEvents() {
        // This will be implemented with the background sync worker
    }
    
    override suspend fun getEventsByCategory(category: EventCategory): Flow<List<Event>> = flow {
        try {
            val snapshot = firestore.collection("events")
                .whereEqualTo("category", category.name)
                .whereEqualTo("isActive", true)
                .get().await()
            
            val events = snapshot.documents.mapNotNull { it.toObject(Event::class.java) }
            emit(events)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    override suspend fun getLiveEvents(location: GeoPoint): Flow<List<Event>> = flow {
        try {
            val now = System.currentTimeMillis()
            val snapshot = firestore.collection("events")
                .whereEqualTo("isActive", true)
                .whereLessThanOrEqualTo("startDate", now)
                .whereGreaterThanOrEqualTo("endDate", now)
                .get().await()
            
            val events = mutableListOf<Event>()
            for (document in snapshot.documents) {
                val event = document.toObject(Event::class.java)
                if (event != null) {
                    val distance = calculateDistance(
                        location.latitude, location.longitude,
                        event.location.latitude, event.location.longitude
                    )
                    if (distance <= 50.0) { // 50km radius for live events
                        events.add(event)
                    }
                }
            }
            
            emit(events)
        } catch (e: Exception) {
            emit(emptyList())
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
