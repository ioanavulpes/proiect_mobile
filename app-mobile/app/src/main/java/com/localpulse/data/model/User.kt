package com.localpulse.data.model

import com.google.firebase.Timestamp

/**
 * User data model for Firestore
 */
data class User(
    val uid: String = "",
    val email: String = "",
    val createdAt: Timestamp? = null
)

