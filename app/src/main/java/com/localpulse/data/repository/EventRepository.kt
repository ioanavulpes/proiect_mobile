package com.localpulse.data.repository

import com.localpulse.data.model.Event
import com.localpulse.data.model.EventCategory
import com.localpulse.data.model.GeoPoint
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    suspend fun getNearbyEvents(
        location: GeoPoint,
        radiusKm: Double,
        categories: List<EventCategory> = emptyList(),
        maxPrice: Double? = null,
        timeRange: TimeRange? = null
    ): Flow<List<Event>>
    
    suspend fun getEventById(eventId: String): Event?
    suspend fun searchEvents(query: String, location: GeoPoint): Flow<List<Event>>
    suspend fun syncEvents()
    suspend fun getEventsByCategory(category: EventCategory): Flow<List<Event>>
    suspend fun getLiveEvents(location: GeoPoint): Flow<List<Event>>
}

data class TimeRange(
    val start: Long,
    val end: Long
)
