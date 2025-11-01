package com.localpulse.data.model

import com.google.firebase.firestore.GeoPoint
import java.util.Date

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val address: String = "",
    val city: String = "",
    val startDate: Date = Date(),
    val endDate: Date = Date(),
    val category: EventCategory = EventCategory.OTHER,
    val price: Double = 0.0,
    val currency: String = "RON",
    val imageUrl: String = "",
    val organizer: String = "",
    val organizerId: String = "",
    val maxAttendees: Int = 0,
    val currentAttendees: Int = 0,
    val isOnline: Boolean = false,
    val onlineUrl: String = "",
    val tags: List<String> = emptyList(),
    val source: EventSource = EventSource.LOCAL,
    val externalId: String = "",
    val isActive: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class EventCategory {
    MUSIC,
    TECH,
    SPORTS,
    FOOD,
    ART,
    EDUCATION,
    BUSINESS,
    HEALTH,
    FAMILY,
    NIGHTLIFE,
    OTHER
}

enum class EventSource {
    EVENTBRITE,
    MEETUP,
    FACEBOOK,
    OPEN_DATA,
    LOCAL
}
