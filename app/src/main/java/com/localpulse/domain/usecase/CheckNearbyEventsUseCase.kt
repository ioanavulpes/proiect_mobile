package com.localpulse.domain.usecase

import com.localpulse.data.model.Event
import com.localpulse.data.model.GeoPoint
import com.localpulse.data.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckNearbyEventsUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(
        location: GeoPoint,
        radiusKm: Double = 1.0
    ): Flow<List<Event>> {
        return eventRepository.getNearbyEvents(
            location = location,
            radiusKm = radiusKm
        )
    }
}
