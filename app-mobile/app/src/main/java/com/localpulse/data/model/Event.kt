package com.localpulse.data.model

import kotlinx.serialization.Serializable

/**
 * Event data model representing an event from Eventbrite API
 */
@Serializable
data class Event(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val url: String = "",
    val image: String? = null,
    val startTime: String = "",
    val venueName: String = "",
    val venueAddress: String = ""
)

