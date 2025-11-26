package com.localpulse.ui.map

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.localpulse.R
import com.localpulse.data.model.Event
import com.localpulse.util.Resource
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Map screen with event locations and travel time calculation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val eventsState by viewModel.eventsState.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val selectedEvent by viewModel.selectedEvent.collectAsState()
    val travelInfo by viewModel.travelInfo.collectAsState()
    val locationPermissionGranted by viewModel.locationPermissionGranted.collectAsState()
    val searchedCity by viewModel.searchedCity.collectAsState()
    val geocodedLocation by viewModel.geocodedLocation.collectAsState()

    // Search dialog state
    var showSearchDialog by remember { mutableStateOf(false) }

    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        
        android.util.Log.d("MapScreen", "📍 Permission result - Fine: $fineGranted, Coarse: $coarseGranted")
        
        if (fineGranted || coarseGranted) {
            viewModel.setLocationPermissionGranted(true)
        } else {
            android.util.Log.w("MapScreen", "⚠️ Location permission denied by user")
        }
    }

    // Request location on screen load - runs every time screen is displayed
    LaunchedEffect(locationPermissionGranted) {
        android.util.Log.d("MapScreen", "🗺️ Map screen loaded. Permission granted: $locationPermissionGranted")
        
        if (!locationPermissionGranted) {
            android.util.Log.d("MapScreen", "📋 Requesting location permissions...")
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            android.util.Log.d("MapScreen", "✅ Permission already granted, getting location...")
            viewModel.getCurrentLocation()
        }
    }

    // City search dialog
    if (showSearchDialog) {
        CitySearchDialog(
            currentCity = searchedCity,
            onDismiss = { showSearchDialog = false },
            onSearch = { city ->
                showSearchDialog = false
                viewModel.searchCity(city)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(stringResource(R.string.map))
                        Text(
                            text = "${stringResource(R.string.current_city)}: $searchedCity",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Search city button
                    IconButton(onClick = { showSearchDialog = true }) {
                        Icon(Icons.Default.Search, contentDescription = "Search City")
                    }
                    // Refresh location button
                    IconButton(
                        onClick = {
                            android.util.Log.d("MapScreen", "🔄 Refresh location button clicked")
                            if (locationPermissionGranted) {
                                viewModel.getCurrentLocation()
                            } else {
                                android.util.Log.d("MapScreen", "❌ No permission, requesting...")
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh Location")
                    }
                    // My location button
                    IconButton(
                        onClick = {
                            android.util.Log.d("MapScreen", "📍 My location button clicked")
                            if (locationPermissionGranted) {
                                viewModel.getCurrentLocation()
                                // Center map on user location if available
                                currentLocation?.let { loc ->
                                    android.util.Log.d("MapScreen", "Centering on user: ${loc.latitude}, ${loc.longitude}")
                                }
                            } else {
                                android.util.Log.d("MapScreen", "❌ No permission, requesting...")
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        }
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = "My Location")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Save state to local variable to enable smart cast
            val currentEventsState = eventsState
            
            when (currentEventsState) {
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is Resource.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = currentEventsState.message ?: stringResource(R.string.error_fetching_events),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadEvents() }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
                is Resource.Success -> {
                    val events = currentEventsState.data
                    val eventsWithLocation = events.filter { it.latitude != null && it.longitude != null }
                    
                    // Debug logging
                    android.util.Log.d("MapScreen", "Total events: ${events.size}, Events with location: ${eventsWithLocation.size}")
                    events.forEachIndexed { index, event ->
                        android.util.Log.d("MapScreen", "Event $index: ${event.name} - lat: ${event.latitude}, lng: ${event.longitude}")
                    }

                    if (eventsWithLocation.isEmpty()) {
                        // No events with location
                        NoEventsMessage(
                            totalEvents = events.size,
                            onRefresh = { viewModel.loadEvents() }
                        )
                    } else {
                        // Show map
                        MapContent(
                            events = eventsWithLocation,
                            currentLocation = currentLocation,
                            geocodedLocation = geocodedLocation,
                            viewModel = viewModel
                        )
                    }
                }
            }

            // Location permission status card
            if (!locationPermissionGranted) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .fillMaxWidth(0.9f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Location Permission Required",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.location_permission_needed),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                android.util.Log.d("MapScreen", "📋 Permission button clicked from card")
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.enable_location))
                        }
                    }
                }
            }
            
            // Show current location indicator when available
            if (locationPermissionGranted && currentLocation != null) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "📍 ${String.format("%.4f", currentLocation!!.latitude)}, ${String.format("%.4f", currentLocation!!.longitude)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MapContent(
    events: List<Event>,
    currentLocation: android.location.Location?,
    geocodedLocation: LatLng?,
    viewModel: MapViewModel
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()

    // Auto-adjust camera with improved positioning logic
    LaunchedEffect(events, currentLocation, geocodedLocation) {
        if (events.isEmpty()) return@LaunchedEffect
        
        try {
            val target = when {
                // Priority 1: Use geocoded location if available
                geocodedLocation != null -> {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(geocodedLocation, 12f)
                    return@LaunchedEffect
                }
                
                // Priority 2: Use user's actual location if within reasonable distance of events
                currentLocation != null && isNearEvents(currentLocation, events) -> {
                    LatLng(currentLocation.latitude, currentLocation.longitude)
                }
                
                // Priority 3: Center on events
                events.isNotEmpty() -> {
                    calculateEventsCenterPoint(events)
                }
                
                // Fallback
                else -> LatLng(51.5074, -0.1278) // London default
            }
            
            cameraPositionState.position = CameraPosition.fromLatLngZoom(target, 12f)
        } catch (e: Exception) {
            android.util.Log.e("MapScreen", "Error positioning camera", e)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = currentLocation != null
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = false,
                zoomGesturesEnabled = true,
                scrollGesturesEnabled = true,
                tiltGesturesEnabled = true
            )
        ) {
            // Add markers with InfoWindow for events
            events.forEach { event ->
                if (event.latitude != null && event.longitude != null) {
                    val distance = viewModel.calculateDistanceToEvent(event)
                    
                    MarkerInfoWindowContent(
                        state = MarkerState(position = LatLng(event.latitude, event.longitude)),
                        onInfoWindowClick = {
                            // Open event URL in browser
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                android.util.Log.e("MapScreen", "Error opening URL", e)
                            }
                        }
                    ) {
                        // Custom InfoWindow content
                        EventInfoWindow(
                            event = event,
                            distance = distance
                        )
                    }
                }
            }
        }
        
        // Floating action button to recenter map
        FloatingActionButton(
            onClick = {
                if (events.isNotEmpty()) {
                    val center = calculateEventsCenterPoint(events)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(center, 12f)
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.CenterFocusStrong,
                contentDescription = "Recenter Map",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun TravelInfoCard(
    event: Event,
    travelInfo: TravelInfo,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text(
                text = event.venueName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Distance
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${stringResource(R.string.distance)}: ${String.format("%.1f", travelInfo.distanceKm)} km",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Car time
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${stringResource(R.string.by_car)}: ${formatTime(travelInfo.carTimeMinutes)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Walking time
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DirectionsWalk,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${stringResource(R.string.walking)}: ${formatTime(travelInfo.walkingTimeMinutes)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun EventInfoCard(
    event: Event,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            Text(
                text = event.venueName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.enable_location),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun NoEventsMessage(
    totalEvents: Int = 0,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.EventBusy,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (totalEvents > 0) {
                "$totalEvents evenimente găsite, dar fără coordonate GPS"
            } else {
                stringResource(R.string.no_events_found)
            },
            style = MaterialTheme.typography.titleMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Încearcă cu orașe mari: New York, London, Paris",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRefresh) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.retry))
        }
    }
}

/**
 * Format time in minutes to human-readable string
 */
private fun formatTime(minutes: Int): String {
    return when {
        minutes < 60 -> "$minutes min"
        else -> {
            val hours = minutes / 60
            val mins = minutes % 60
            if (mins == 0) "$hours h" else "$hours h $mins min"
        }
    }
}

/**
 * Custom InfoWindow content for event markers
 */
@Composable
private fun EventInfoWindow(
    event: Event,
    distance: Double?
) {
    Card(
        modifier = Modifier.width(280.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = event.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = event.venueName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = event.venueAddress,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
            
            if (distance != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = String.format("%.1f km away", distance),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = stringResource(R.string.tap_to_view_details),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * City search dialog
 */
@Composable
private fun CitySearchDialog(
    currentCity: String,
    onDismiss: () -> Unit,
    onSearch: (String) -> Unit
) {
    var searchText by remember { mutableStateOf(currentCity) }
    val popularCities = com.localpulse.util.Constants.POPULAR_CITIES.filter { it != "All Cities" }
    val filteredCities = remember(searchText) {
        if (searchText.isBlank()) {
            popularCities
        } else {
            popularCities.filter { it.contains(searchText, ignoreCase = true) }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.search_city)) },
        text = {
            Column {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text(stringResource(R.string.search_city_hint)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Popular cities:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // List of cities
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(filteredCities) { city ->
                        TextButton(
                            onClick = {
                                searchText = city
                                onSearch(city)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = city,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Start
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSearch(searchText) }) {
                Text(stringResource(R.string.search_city_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Check if user location is near events (within 100km)
 */
private fun isNearEvents(
    location: android.location.Location,
    events: List<Event>
): Boolean {
    val MAX_DISTANCE_KM = 100.0
    
    return events.any { event ->
        if (event.latitude != null && event.longitude != null) {
            val distance = calculateDistance(
                location.latitude,
                location.longitude,
                event.latitude,
                event.longitude
            )
            distance <= MAX_DISTANCE_KM
        } else {
            false
        }
    }
}

/**
 * Calculate center point of all events
 */
private fun calculateEventsCenterPoint(events: List<Event>): LatLng {
    val validEvents = events.filter { it.latitude != null && it.longitude != null }
    
    if (validEvents.isEmpty()) {
        return LatLng(51.5074, -0.1278) // London default
    }
    
    val avgLat = validEvents.mapNotNull { it.latitude }.average()
    val avgLng = validEvents.mapNotNull { it.longitude }.average()
    
    return LatLng(avgLat, avgLng)
}

/**
 * Calculate distance using Haversine formula
 */
private fun calculateDistance(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double
): Double {
    val earthRadius = 6371.0 // km
    
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    
    val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
            kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
            kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
    
    val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
    
    return earthRadius * c
}
