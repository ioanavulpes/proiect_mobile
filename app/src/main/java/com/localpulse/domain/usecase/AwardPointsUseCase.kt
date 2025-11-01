package com.localpulse.domain.usecase

import com.localpulse.data.model.User
import com.localpulse.data.repository.GamificationRepository
import javax.inject.Inject

class AwardPointsUseCase @Inject constructor(
    private val gamificationRepository: GamificationRepository
) {
    suspend operator fun invoke(
        userId: String,
        action: PointAction,
        eventId: String? = null
    ): Result<Int> {
        return try {
            val points = gamificationRepository.awardPoints(
                userId = userId,
                action = action,
                eventId = eventId
            )
            Result.success(points)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

enum class PointAction {
    EVENT_CHECK_IN,
    EVENT_CREATION,
    CHAT_MESSAGE,
    PROFILE_COMPLETION,
    FIRST_EVENT,
    WEEKLY_STREAK,
    MONTHLY_ACTIVE
}
