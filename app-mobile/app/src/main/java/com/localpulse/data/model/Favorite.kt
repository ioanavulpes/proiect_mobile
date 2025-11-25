package com.localpulse.data.model

import com.google.firebase.Timestamp

/**
 * Favorite data model for Firestore
 * Represents a user's saved event
 */
data class Favorite(
    val id: String = "",
    val userId: String = "",
    val eventId: String = "",
    val eventName: String = "",
    val eventImage: String? = null,
    val eventUrl: String = "",
    val timestamp: Timestamp? = null
)

