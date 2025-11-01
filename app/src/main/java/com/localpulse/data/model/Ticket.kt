package com.localpulse.data.model

import java.util.Date

data class Ticket(
    val id: String = "",
    val eventId: String = "",
    val userId: String = "",
    val qrCode: String = "",
    val status: TicketStatus = TicketStatus.PENDING,
    val purchaseDate: Date = Date(),
    val price: Double = 0.0,
    val currency: String = "RON",
    val externalId: String = "",
    val externalUrl: String = "",
    val checkInDate: Date? = null,
    val checkInLocation: GeoPoint? = null,
    val isTransferable: Boolean = false,
    val transferTo: String? = null,
    val transferDate: Date? = null
)

data class GeoPoint(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

enum class TicketStatus {
    PENDING,
    CONFIRMED,
    CHECKED_IN,
    CANCELLED,
    REFUNDED,
    TRANSFERRED
}
