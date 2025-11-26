package com.localpulse.ui.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.localpulse.data.repository.EventRepository

/**
 * Factory for creating MapViewModel with dependencies
 */
class MapViewModelFactory(
    private val eventRepository: EventRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(eventRepository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

