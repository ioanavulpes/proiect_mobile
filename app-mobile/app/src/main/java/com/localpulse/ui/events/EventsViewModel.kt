package com.localpulse.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.localpulse.data.model.Event
import com.localpulse.data.repository.EventRepository
import com.localpulse.data.repository.FavoritesRepository
import com.localpulse.util.Constants
import com.localpulse.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing events data
 */
class EventsViewModel(
    private val eventRepository: EventRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _eventsState = MutableStateFlow<Resource<List<Event>>>(Resource.Loading)
    val eventsState: StateFlow<Resource<List<Event>>> = _eventsState.asStateFlow()

    private val _eventDetailsState = MutableStateFlow<Resource<Event>?>(null)
    val eventDetailsState: StateFlow<Resource<Event>?> = _eventDetailsState.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    init {
        searchEvents(Constants.DEFAULT_CITY)
    }

    /**
     * Search for events with filters
     */
    fun searchEvents(
        city: String,
        keyword: String = "",
        category: String = "",
        startDate: String = "",
        endDate: String = ""
    ) {
        viewModelScope.launch {
            _eventsState.value = Resource.Loading
            _eventsState.value = eventRepository.searchEvents(city, keyword, category, startDate, endDate)
        }
    }

    /**
     * Get event details by ID
     */
    fun getEventDetails(eventId: String) {
        viewModelScope.launch {
            _eventDetailsState.value = Resource.Loading
            _eventDetailsState.value = eventRepository.getEventById(eventId)
        }
    }

    /**
     * Toggle favorite status for an event
     */
    fun toggleFavorite(event: Event) {
        viewModelScope.launch {
            val isFavorite = favoritesRepository.isFavorite(event.id)
            if (isFavorite) {
                favoritesRepository.removeFavorite(event.id)
                _favoriteIds.value = _favoriteIds.value - event.id
            } else {
                favoritesRepository.addFavorite(event)
                _favoriteIds.value = _favoriteIds.value + event.id
            }
        }
    }

    /**
     * Check favorite status for an event
     */
    fun checkFavoriteStatus(eventId: String) {
        viewModelScope.launch {
            val isFavorite = favoritesRepository.isFavorite(eventId)
            if (isFavorite) {
                _favoriteIds.value = _favoriteIds.value + eventId
            }
        }
    }

    /**
     * Refresh favorite status for all events
     */
    fun refreshFavoriteStatuses(events: List<Event>) {
        viewModelScope.launch {
            val favorites = mutableSetOf<String>()
            events.forEach { event ->
                if (favoritesRepository.isFavorite(event.id)) {
                    favorites.add(event.id)
                }
            }
            _favoriteIds.value = favorites
        }
    }
}

/**
 * Factory for creating EventsViewModel with dependencies
 */
class EventsViewModelFactory(
    private val eventRepository: EventRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventsViewModel(eventRepository, favoritesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

