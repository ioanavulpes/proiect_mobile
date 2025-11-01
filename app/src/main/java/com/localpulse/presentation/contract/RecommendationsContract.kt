package com.localpulse.presentation.contract

import com.localpulse.data.model.Event
import kotlinx.coroutines.flow.StateFlow

interface RecommendationsContract {
    data class ViewState(
        val recommendations: List<Event> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )
    
    interface View {
        val viewState: StateFlow<ViewState>
        fun loadRecommendations()
        fun refreshRecommendations()
        fun onEventClick(eventId: String)
    }
}
