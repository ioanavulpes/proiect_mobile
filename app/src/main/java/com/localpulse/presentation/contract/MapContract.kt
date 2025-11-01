package com.localpulse.presentation.contract

import com.localpulse.data.model.Event
import kotlinx.coroutines.flow.StateFlow

interface MapContract {
    data class ViewState(
        val events: List<Event> = emptyList(),
        val isLoading: Boolean = false,
        val showFilters: Boolean = false,
        val error: String? = null
    )
    
    interface View {
        val viewState: StateFlow<ViewState>
        fun loadEvents()
        fun toggleFilters()
        fun hideFilters()
        fun applyFilters(categories: List<String>, distance: Double, price: Double?)
    }
}
