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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()

    // Track current search parameters for reuse
    private var currentCity = Constants.DEFAULT_CITY
    private var currentKeyword = ""
    private var currentCategory = ""

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
        // Store current search parameters
        currentCity = city
        currentKeyword = keyword
        currentCategory = category
        
        viewModelScope.launch {
            _eventsState.value = Resource.Loading
            
            // Convert selected date to ISO 8601 format if present
            val (actualStartDate, actualEndDate) = if (_selectedDate.value != null) {
                val date = _selectedDate.value!!
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                val start = "${date}T00:00:00Z"
                val end = "${date}T23:59:59Z"
                Pair(start, end)
            } else {
                Pair(startDate, endDate)
            }
            
            _eventsState.value = eventRepository.searchEvents(
                city, 
                keyword, 
                category, 
                actualStartDate, 
                actualEndDate
            )
        }
    }
    
    /**
     * Set selected date for filtering
     */
    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        // Re-run search with new date filter
        searchEvents(currentCity, currentKeyword, currentCategory)
    }
    
    /**
     * Clear date filter
     */
    fun clearDateFilter() {
        _selectedDate.value = null
        // Re-run search without date filter
        searchEvents(currentCity, currentKeyword, currentCategory)
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

