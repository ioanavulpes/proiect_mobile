package com.localpulse.domain.usecase

import com.localpulse.data.model.Event
import com.localpulse.data.model.GeoPoint
import com.localpulse.data.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNearbyEventsUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(
        location: GeoPoint,
        radiusKm: Double = 10.0,
        categories: List<com.localpulse.data.model.EventCategory> = emptyList(),
        maxPrice: Double? = null,
        timeRange: TimeRange? = null
    ): Flow<List<Event>> {
        return eventRepository.getNearbyEvents(
            location = location,
            radiusKm = radiusKm,
            categories = categories,
            maxPrice = maxPrice,
            timeRange = timeRange
        )
    }
}

data class TimeRange(
    val start: Long,
    val end: Long
)
