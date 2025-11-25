package com.localpulse.data.repository

import android.content.Context
import com.localpulse.data.model.Event
import com.localpulse.data.network.TicketmasterApiService
import com.localpulse.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for handling event data from Ticketmaster API
 */
class EventRepository(context: Context) {
    private val apiService = TicketmasterApiService(context)

    /**
     * Fetch events from Eventbrite API
     */
    suspend fun searchEvents(city: String): Resource<List<Event>> {
        return withContext(Dispatchers.IO) {
            try {
                val events = apiService.searchEvents(city)
                Resource.Success(events)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to fetch events")
            }
        }
    }

    /**
     * Get a single event by ID
     */
    suspend fun getEventById(eventId: String): Resource<Event> {
        return withContext(Dispatchers.IO) {
            try {
                val event = apiService.getEventById(eventId)
                if (event != null) {
                    Resource.Success(event)
                } else {
                    Resource.Error("Event not found")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to fetch event")
            }
        }
    }
}

