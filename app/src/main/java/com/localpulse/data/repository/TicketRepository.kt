package com.localpulse.data.repository

import com.localpulse.data.model.GeoPoint
import com.localpulse.data.model.Ticket
import kotlinx.coroutines.flow.Flow

interface TicketRepository {
    suspend fun validateTicket(
        qrCode: String,
        eventId: String,
        checkInLocation: GeoPoint? = null
    ): Ticket
    
    suspend fun getUserTickets(userId: String): Flow<List<Ticket>>
    suspend fun purchaseTicket(eventId: String, userId: String): Result<Ticket>
    suspend fun cancelTicket(ticketId: String): Result<Boolean>
    suspend fun transferTicket(ticketId: String, toUserId: String): Result<Boolean>
    suspend fun generateQRCode(ticketId: String): String
}
