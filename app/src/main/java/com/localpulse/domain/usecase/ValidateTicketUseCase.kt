package com.localpulse.domain.usecase

import com.localpulse.data.model.Ticket
import com.localpulse.data.repository.TicketRepository
import javax.inject.Inject

class ValidateTicketUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(
        qrCode: String,
        eventId: String,
        checkInLocation: com.localpulse.data.model.GeoPoint? = null
    ): Result<Ticket> {
        return try {
            val ticket = ticketRepository.validateTicket(
                qrCode = qrCode,
                eventId = eventId,
                checkInLocation = checkInLocation
            )
            Result.success(ticket)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
