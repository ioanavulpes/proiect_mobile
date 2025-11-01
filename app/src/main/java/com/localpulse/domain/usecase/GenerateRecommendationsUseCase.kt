package com.localpulse.domain.usecase

import com.localpulse.data.model.Event
import com.localpulse.data.model.User
import com.localpulse.data.repository.UserBehaviorRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GenerateRecommendationsUseCase @Inject constructor(
    private val userBehaviorRepository: UserBehaviorRepository
) {
    suspend operator fun invoke(
        user: User,
        currentLocation: com.localpulse.data.model.GeoPoint,
        limit: Int = 20
    ): Flow<List<Event>> {
        return userBehaviorRepository.generateRecommendations(
            user = user,
            currentLocation = currentLocation,
            limit = limit
        )
    }
}
