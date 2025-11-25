package com.localpulse.data.network

import android.content.Context
import com.localpulse.R
import com.localpulse.data.model.Event
import com.localpulse.util.Constants
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Service class for interacting with the Ticketmaster Discovery API v2
 * Uses OkHttp for HTTP requests and Kotlinx Serialization for JSON parsing
 */
class TicketmasterApiService(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val client: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Fetches events from Ticketmaster API based on city location
     * @param city The city to search for events
     * @return List of Event objects
     */
    suspend fun searchEvents(city: String = Constants.DEFAULT_CITY): List<Event> {
        val apiKey = context.getString(R.string.ticketmaster_api_key)
        
        // Build URL with query parameters
        val url = "${Constants.TICKETMASTER_BASE_URL}${Constants.TICKETMASTER_EVENTS_ENDPOINT}" +
                "?apikey=$apiKey" +
                "&city=$city" +
                "&size=${Constants.DEFAULT_PAGE_SIZE}"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return try {
            // Debug logging
            android.util.Log.d("TicketmasterAPI", "Request URL: $url")
            android.util.Log.d("TicketmasterAPI", "API Key (first 10 chars): ${apiKey.take(10)}...")
            android.util.Log.d("TicketmasterAPI", "API Key length: ${apiKey.length}")
            
            val response = client.newCall(request).execute()
            
            android.util.Log.d("TicketmasterAPI", "Response code: ${response.code}")
            android.util.Log.d("TicketmasterAPI", "Response message: ${response.message}")
            
            if (!response.isSuccessful) {
                // Log response body for debugging
                val errorBody = response.body?.string()
                android.util.Log.e("TicketmasterAPI", "Error body: $errorBody")
                throw Exception("API Error: ${response.code} ${response.message}")
            }

            val responseBody = response.body?.string() ?: throw Exception("Empty response body")
            
            android.util.Log.d("TicketmasterAPI", "Response body (first 500 chars): ${responseBody.take(500)}")
            
            val ticketmasterResponse = json.decodeFromString<TicketmasterResponse>(responseBody)
            
            // Convert Ticketmaster events to our Event model
            ticketmasterResponse.embedded?.events?.map { ticketmasterEvent ->
                // Get the best quality image
                val imageUrl = ticketmasterEvent.images
                    ?.sortedByDescending { it.width ?: 0 }
                    ?.firstOrNull()?.url
                
                // Get venue information
                val venue = ticketmasterEvent.embedded?.venues?.firstOrNull()
                val venueName = venue?.name ?: "TBA"
                val venueAddress = buildString {
                    venue?.address?.line1?.let { append(it) }
                    venue?.city?.name?.let { 
                        if (isNotEmpty()) append(", ")
                        append(it)
                    }
                    venue?.state?.name?.let {
                        if (isNotEmpty()) append(", ")
                        append(it)
                    }
                }.ifEmpty { "Address not available" }
                
                // Format date and time
                val dateTime = ticketmasterEvent.dates?.start?.let { start ->
                    buildString {
                        start.localDate?.let { append(it) }
                        start.localTime?.let { 
                            if (isNotEmpty()) append(" ")
                            append(it)
                        }
                    }
                } ?: "Date TBA"
                
                // Combine info and pleaseNote for description
                val description = buildString {
                    ticketmasterEvent.info?.let { append(it) }
                    ticketmasterEvent.pleaseNote?.let {
                        if (isNotEmpty()) append("\n\n")
                        append("Note: $it")
                    }
                }.ifEmpty { null }
                
                Event(
                    id = ticketmasterEvent.id,
                    name = ticketmasterEvent.name,
                    description = description,
                    url = ticketmasterEvent.url,
                    image = imageUrl,
                    startTime = dateTime,
                    venueName = venueName,
                    venueAddress = venueAddress
                )
            } ?: emptyList()
            
        } catch (e: Exception) {
            // Log error and return empty list
            e.printStackTrace()
            android.util.Log.e("TicketmasterAPI", "Exception: ${e.message}", e)
            throw Exception("Failed to fetch events: ${e.message}")
        }
    }

    /**
     * Fetches a single event by ID
     * Note: This searches through all events. In production, use the specific event endpoint.
     */
    suspend fun getEventById(eventId: String): Event? {
        return try {
            searchEvents().find { it.id == eventId }
        } catch (e: Exception) {
            null
        }
    }
}

