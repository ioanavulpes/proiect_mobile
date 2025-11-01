package com.localpulse.data.repository.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.localpulse.data.model.GeoPoint
import com.localpulse.data.model.Ticket
import com.localpulse.data.model.TicketStatus
import com.localpulse.data.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseTicketRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : TicketRepository {
    
    override suspend fun validateTicket(
        qrCode: String,
        eventId: String,
        checkInLocation: GeoPoint?
    ): Ticket {
        val ticketQuery = firestore.collection("tickets")
            .whereEqualTo("qrCode", qrCode)
            .whereEqualTo("eventId", eventId)
            .whereEqualTo("status", TicketStatus.CONFIRMED)
            .limit(1)
            .get()
            .await()
        
        if (ticketQuery.isEmpty) {
            throw Exception("Invalid ticket")
        }
        
        val ticketDoc = ticketQuery.documents.first()
        val ticket = ticketDoc.toObject(Ticket::class.java) ?: throw Exception("Invalid ticket data")
        
        // Update ticket status to checked in
        val updatedTicket = ticket.copy(
            status = TicketStatus.CHECKED_IN,
            checkInDate = java.util.Date(),
            checkInLocation = checkInLocation
        )
        
        ticketDoc.reference.set(updatedTicket).await()
        
        return updatedTicket
    }
    
    override suspend fun getUserTickets(userId: String): Flow<List<Ticket>> = flow {
        try {
            val snapshot = firestore.collection("tickets")
                .whereEqualTo("userId", userId)
                .orderBy("purchaseDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            
            val tickets = snapshot.documents.mapNotNull { it.toObject(Ticket::class.java) }
            emit(tickets)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    override suspend fun purchaseTicket(eventId: String, userId: String): Result<Ticket> {
        return try {
            // TODO: Integrate with payment gateway (Stripe, PayPal, etc.)
            val ticket = Ticket(
                eventId = eventId,
                userId = userId,
                qrCode = generateQRCode(),
                status = TicketStatus.PENDING
            )
            
            val docRef = firestore.collection("tickets").add(ticket).await()
            val ticketWithId = ticket.copy(id = docRef.id)
            
            // Update the document with the ID
            docRef.set(ticketWithId).await()
            
            Result.success(ticketWithId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun cancelTicket(ticketId: String): Result<Boolean> {
        return try {
            firestore.collection("tickets")
                .document(ticketId)
                .update("status", TicketStatus.CANCELLED)
                .await()
            
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun transferTicket(ticketId: String, toUserId: String): Result<Boolean> {
        return try {
            firestore.collection("tickets")
                .document(ticketId)
                .update(
                    mapOf(
                        "transferTo" to toUserId,
                        "transferDate" to java.util.Date(),
                        "isTransferable" to true
                    )
                )
                .await()
            
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun generateQRCode(ticketId: String): String {
        // Generate a unique QR code for the ticket
        return "LP_${ticketId}_${System.currentTimeMillis()}"
    }
    
    private fun generateQRCode(): String {
        return "LP_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
}
