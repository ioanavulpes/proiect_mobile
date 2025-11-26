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
     * Fetches events from Ticketmaster API with advanced filters
     * @param city The city to search for events
     * @param keyword Search keyword (artist, event name, venue)
     * @param category Event category (music, sports, arts, etc.)
     * @param startDate Start date in format YYYY-MM-DDTHH:mm:ssZ
     * @param endDate End date in format YYYY-MM-DDTHH:mm:ssZ
     * @return List of Event objects
     */
    suspend fun searchEvents(
        city: String = Constants.DEFAULT_CITY,
        keyword: String = "",
        category: String = "",
        startDate: String = "",
        endDate: String = ""
    ): List<Event> {
        val apiKey = context.getString(R.string.ticketmaster_api_key)
        
        // Build URL with query parameters
        val urlBuilder = StringBuilder()
        urlBuilder.append("${Constants.TICKETMASTER_BASE_URL}${Constants.TICKETMASTER_EVENTS_ENDPOINT}")
        urlBuilder.append("?apikey=$apiKey")
        urlBuilder.append("&size=${Constants.DEFAULT_PAGE_SIZE}")
        
        // Add optional parameters
        if (city.isNotBlank() && city != "All Cities") {
            // Try countryCode for better Romania coverage
            val romanianCities = listOf("Bucharest", "Cluj-Napoca", "Timisoara", "Iasi", "Brasov", "Constanta")
            if (romanianCities.any { it.equals(city, ignoreCase = true) }) {
                urlBuilder.append("&countryCode=RO")
            }
            urlBuilder.append("&city=$city")
        }
        if (keyword.isNotBlank()) {
            urlBuilder.append("&keyword=$keyword")
        }
        if (category.isNotBlank()) {
            urlBuilder.append("&classificationName=$category")
        }
        if (startDate.isNotBlank()) {
            urlBuilder.append("&startDateTime=$startDate")
        }
        if (endDate.isNotBlank()) {
            urlBuilder.append("&endDateTime=$endDate")
        }
        
        val url = urlBuilder.toString()

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
            
            android.util.Log.d("TicketmasterAPI", "Response body (first 1000 chars): ${responseBody.take(1000)}")
            
            // Check if we have events
            if (responseBody.contains("\"_embedded\"")) {
                android.util.Log.d("TicketmasterAPI", "✅ Events found in response!")
            } else {
                android.util.Log.w("TicketmasterAPI", "⚠️ No _embedded field found - likely no events for this search")
            }
            
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
                
                // Extract latitude and longitude from venue location
                val latitude = venue?.location?.latitude?.toDoubleOrNull()
                val longitude = venue?.location?.longitude?.toDoubleOrNull()
                
                Event(
                    id = ticketmasterEvent.id,
                    name = ticketmasterEvent.name,
                    description = description,
                    url = ticketmasterEvent.url,
                    image = imageUrl,
                    startTime = dateTime,
                    venueName = venueName,
                    venueAddress = venueAddress,
                    latitude = latitude,
                    longitude = longitude
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

