package com.localpulse.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.maps.model.LatLng
import com.localpulse.R
import com.localpulse.data.model.Event
import com.localpulse.data.model.SearchFilters
import com.localpulse.data.network.GoogleDistanceMatrixService
import com.localpulse.data.repository.EventRepository
import com.localpulse.util.GeocodingService
import com.localpulse.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * ViewModel for map screen with location-based features
 */
class MapViewModel(
    private val eventRepository: EventRepository,
    context: Context
) : ViewModel() {

    private val _eventsState: MutableStateFlow<Resource<List<Event>>> = 
        MutableStateFlow(Resource.Success(emptyList()))
    val eventsState: StateFlow<Resource<List<Event>>> = _eventsState.asStateFlow()

    private val _currentLocation: MutableStateFlow<Location?> = MutableStateFlow(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _selectedEvent: MutableStateFlow<Event?> = MutableStateFlow(null)
    val selectedEvent: StateFlow<Event?> = _selectedEvent.asStateFlow()

    private val _travelInfo: MutableStateFlow<TravelInfo?> = MutableStateFlow(null)
    val travelInfo: StateFlow<TravelInfo?> = _travelInfo.asStateFlow()

    private val _locationPermissionGranted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val locationPermissionGranted: StateFlow<Boolean> = _locationPermissionGranted.asStateFlow()

    private val _searchedCity: MutableStateFlow<String> = MutableStateFlow("")
    val searchedCity: StateFlow<String> = _searchedCity.asStateFlow()

    private val _geocodedLocation: MutableStateFlow<LatLng?> = MutableStateFlow(null)
    val geocodedLocation: StateFlow<LatLng?> = _geocodedLocation.asStateFlow()

    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context.applicationContext)
    
    private val geocodingService = GeocodingService(context.applicationContext)
    
    // Use separate API key for Distance Matrix (must have "Application restrictions" = "None")
    private val distanceMatrixApiKey = context.getString(R.string.google_distance_matrix_api_key)
    private val distanceMatrixService = GoogleDistanceMatrixService(distanceMatrixApiKey)
    
    private val appContext = context.applicationContext

    init {
        checkLocationPermission()
        // Only detect user location - DO NOT load events automatically
        // Events will be loaded only when user searches for a city
    }

    /**
     * Check if location permission is granted
     */
    fun checkLocationPermission() {
        val granted = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        _locationPermissionGranted.value = granted
        Log.d(TAG, "Location permission checked: $granted")
    }

    /**
     * Set location permission granted status
     */
    fun setLocationPermissionGranted(granted: Boolean) {
        Log.d(TAG, "Setting location permission: $granted")
        _locationPermissionGranted.value = granted
        if (granted) {
            getCurrentLocation()
        }
    }

    /**
     * Get current location
     */
    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        if (!_locationPermissionGranted.value) {
            Log.w(TAG, "Location permission not granted, cannot get location")
            return
        }

        Log.d(TAG, "Starting location request...")
        viewModelScope.launch {
            try {
                val cancellationToken = CancellationTokenSource()
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationToken.token
                ).await()
                
                if (location != null) {
                    _currentLocation.value = location
                    Log.d(TAG, "✅ Location received: ${location.latitude}, ${location.longitude}")
                    Log.d(TAG, "   Accuracy: ${location.accuracy}m, Provider: ${location.provider}")
                    
                    // Only set location - DO NOT load events automatically
                    // Events will be loaded when user searches for a city
                    
                    // Calculate travel info if an event is selected
                    _selectedEvent.value?.let { event ->
                        calculateTravelInfo(event)
                    }
                } else {
                    Log.w(TAG, "Location is null, trying last known location...")
                    getLastKnownLocation()
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error getting current location: ${e.message}", e)
                Log.d(TAG, "Trying last known location as fallback...")
                getLastKnownLocation()
            }
        }
    }

    /**
     * Get last known location as fallback
     */
    @SuppressLint("MissingPermission")
    private suspend fun getLastKnownLocation() {
        try {
            val location = fusedLocationClient.lastLocation.await()
            if (location != null) {
                _currentLocation.value = location
                Log.d(TAG, "✅ Last known location: ${location.latitude}, ${location.longitude}")
                
                // Only set location - DO NOT load events automatically
                // Events will be loaded when user searches for a city
                
                // Calculate travel info if an event is selected
                _selectedEvent.value?.let { event ->
                    calculateTravelInfo(event)
                }
            } else {
                Log.w(TAG, "❌ No last known location available")
                Log.w(TAG, "Please set location in emulator: Extended Controls > Location")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting last location: ${e.message}", e)
        }
    }

    /**
     * Load events from repository
     */
    fun loadEvents(filters: SearchFilters = SearchFilters()) {
        viewModelScope.launch {
            _eventsState.value = Resource.Loading
            val result = eventRepository.searchEvents(filters)
            _eventsState.value = result
        }
    }

    /**
     * Select an event and calculate travel info
     */
    fun selectEvent(event: Event) {
        _selectedEvent.value = event
        calculateTravelInfo(event)
    }

    /**
     * Calculate travel info using Google Distance Matrix API for real road distances
     */
    private fun calculateTravelInfo(event: Event) {
        val currentLoc = _currentLocation.value
        val eventLat = event.latitude
        val eventLng = event.longitude

        if (currentLoc == null || eventLat == null || eventLng == null) {
            _travelInfo.value = null
            Log.w(TAG, "Cannot calculate travel info: missing location data")
            return
        }

        Log.d(TAG, "==========================================")
        Log.d(TAG, "🚗 CALCULATING TRAVEL INFO")
        Log.d(TAG, "==========================================")
        Log.d(TAG, "From: ${currentLoc.latitude}, ${currentLoc.longitude}")
        Log.d(TAG, "To: $eventLat, $eventLng")
        Log.d(TAG, "Event: ${event.name}")

        viewModelScope.launch {
            try {
                Log.d(TAG, "📍 Step 1: Requesting DRIVING distance...")
                
                // Get driving distance
                val drivingResult = distanceMatrixService.getDistanceAndDuration(
                    originLat = currentLoc.latitude,
                    originLng = currentLoc.longitude,
                    destLat = eventLat,
                    destLng = eventLng,
                    mode = "driving"
                )

                if (drivingResult == null) {
                    Log.w(TAG, "⚠️ Driving distance API returned null")
                    Log.w(TAG, "⚠️ Using FALLBACK calculation (straight line)")
                    calculateTravelInfoFallback(event)
                    return@launch
                }

                Log.d(TAG, "📍 Step 2: Requesting WALKING distance...")

                // Get walking distance
                val walkingResult = distanceMatrixService.getDistanceAndDuration(
                    originLat = currentLoc.latitude,
                    originLng = currentLoc.longitude,
                    destLat = eventLat,
                    destLng = eventLng,
                    mode = "walking"
                )

                if (walkingResult == null) {
                    Log.w(TAG, "⚠️ Walking distance API returned null")
                    Log.w(TAG, "⚠️ Using FALLBACK calculation (straight line)")
                    calculateTravelInfoFallback(event)
                    return@launch
                }

                _travelInfo.value = TravelInfo(
                    distanceKm = drivingResult.distanceKm,
                    distanceText = drivingResult.distanceText,
                    carTimeMinutes = drivingResult.durationMinutes,
                    carTimeText = drivingResult.durationText,
                    walkingTimeMinutes = walkingResult.durationMinutes,
                    walkingTimeText = walkingResult.durationText
                )
                
                Log.d(TAG, "==========================================")
                Log.d(TAG, "✅ SUCCESS - REAL DISTANCES FROM GOOGLE MAPS")
                Log.d(TAG, "==========================================")
                Log.d(TAG, "🚗 Driving: ${drivingResult.distanceText} in ${drivingResult.durationText}")
                Log.d(TAG, "🚶 Walking: ${walkingResult.distanceText} in ${walkingResult.durationText}")
                Log.d(TAG, "==========================================")
                
            } catch (e: Exception) {
                Log.e(TAG, "==========================================")
                Log.e(TAG, "❌ ERROR in calculateTravelInfo")
                Log.e(TAG, "==========================================")
                Log.e(TAG, "Error: ${e.message}", e)
                Log.e(TAG, "⚠️ Using FALLBACK calculation (straight line)")
                calculateTravelInfoFallback(event)
            }
        }
    }

    /**
     * Fallback: Calculate travel info using Haversine formula (straight line)
     * Used when Google Distance Matrix API fails
     */
    private fun calculateTravelInfoFallback(event: Event) {
        val currentLoc = _currentLocation.value
        val eventLat = event.latitude
        val eventLng = event.longitude

        if (currentLoc == null || eventLat == null || eventLng == null) {
            _travelInfo.value = null
            return
        }

        val distance = calculateDistance(
            currentLoc.latitude,
            currentLoc.longitude,
            eventLat,
            eventLng
        )

        // Estimate travel time
        // Average car speed: 60 km/h
        val carTimeMinutes = (distance / 60.0) * 60.0
        
        // Average walking speed: 5 km/h
        val walkingTimeMinutes = (distance / 5.0) * 60.0

        _travelInfo.value = TravelInfo(
            distanceKm = distance,
            distanceText = String.format("~%.1f km", distance),  // ~ indicates approximate
            carTimeMinutes = carTimeMinutes.toInt(),
            carTimeText = "~${carTimeMinutes.toInt()} min",  // ~ indicates approximate
            walkingTimeMinutes = walkingTimeMinutes.toInt(),
            walkingTimeText = "~${walkingTimeMinutes.toInt()} min"  // ~ indicates approximate
        )
        
        Log.d(TAG, "==========================================")
        Log.d(TAG, "⚠️ USING FALLBACK (STRAIGHT LINE)")
        Log.d(TAG, "==========================================")
        Log.d(TAG, "Distance (straight line): ${String.format("%.1f", distance)} km")
        Log.d(TAG, "Estimated car time (60 km/h): ${carTimeMinutes.toInt()} min")
        Log.d(TAG, "Estimated walking time (5 km/h): ${walkingTimeMinutes.toInt()} min")
        Log.d(TAG, "⚠️ This is NOT the real road distance!")
        Log.d(TAG, "⚠️ To get real distances, enable Distance Matrix API")
        Log.d(TAG, "==========================================")
    }

    /**
     * Calculate distance between two points using Haversine formula
     * Returns distance in kilometers
     */
    private fun calculateDistance(
        lat1: Double, 
        lon1: Double, 
        lat2: Double, 
        lon2: Double
    ): Double {
        val earthRadius = 6371.0 // Earth's radius in kilometers

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    /**
     * Search for events in a city with geocoding
     */
    fun searchCity(cityName: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Searching for city: $cityName")
                _searchedCity.value = cityName
                
                // Geocode the city name
                val coordinates = geocodingService.getCoordinates(cityName)
                _geocodedLocation.value = coordinates
                
                if (coordinates != null) {
                    Log.d(TAG, "Geocoded $cityName to: ${coordinates.latitude}, ${coordinates.longitude}")
                } else {
                    Log.w(TAG, "Could not geocode $cityName, searching by name only")
                }
                
                // Load events for the city
                loadEvents(SearchFilters(city = cityName))
            } catch (e: Exception) {
                Log.e(TAG, "Error searching city: ${e.message}", e)
            }
        }
    }

    /**
     * Calculate distance between current location and event
     */
    fun calculateDistanceToEvent(event: Event): Double? {
        val currentLoc = _currentLocation.value
        val eventLat = event.latitude
        val eventLng = event.longitude

        if (currentLoc == null || eventLat == null || eventLng == null) {
            return null
        }

        return calculateDistance(
            currentLoc.latitude,
            currentLoc.longitude,
            eventLat,
            eventLng
        )
    }

    /**
     * Clear selected event
     */
    fun clearSelection() {
        _selectedEvent.value = null
        _travelInfo.value = null
    }

    companion object {
        private const val TAG = "MapViewModel"
    }
}

/**
 * Data class for travel information
 */
data class TravelInfo(
    val distanceKm: Double,
    val distanceText: String,
    val carTimeMinutes: Int,
    val carTimeText: String,
    val walkingTimeMinutes: Int,
    val walkingTimeText: String
)
