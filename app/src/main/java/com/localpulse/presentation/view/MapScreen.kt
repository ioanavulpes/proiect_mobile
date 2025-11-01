package com.localpulse.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.localpulse.data.model.Event
import com.localpulse.data.model.GeoPoint
import com.localpulse.presentation.presenter.MapPresenter
import com.localpulse.presentation.contract.MapContract

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    val presenter = remember { MapPresenter() }
    val viewState by presenter.viewState.collectAsState()
    
    LaunchedEffect(Unit) {
        presenter.loadEvents()
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Google Maps
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = CameraPositionState(
                position = CameraPosition(
                    target = LatLng(44.4268, 26.1025), // Bucharest coordinates
                    zoom = 12f
                )
            ),
            properties = MapProperties(
                isMyLocationEnabled = true,
                mapType = MapType.NORMAL
            )
        ) {
            // Event markers
            viewState.events.forEach { event ->
                Marker(
                    state = MarkerState(
                        position = LatLng(
                            event.location.latitude,
                            event.location.longitude
                        )
                    ),
                    title = event.title,
                    snippet = event.description
                )
            }
        }
        
        // Top App Bar
        TopAppBar(
            title = { Text("LocalPulse") },
            actions = {
                IconButton(onClick = { presenter.toggleFilters() }) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filters")
                }
            },
            modifier = Modifier.align(Alignment.TopCenter)
        )
        
        // Filter Sheet
        if (viewState.showFilters) {
            EventFilterSheet(
                onDismiss = { presenter.hideFilters() },
                onApplyFilters = { categories, distance, price ->
                    presenter.applyFilters(categories, distance, price)
                }
            )
        }
        
        // Loading indicator
        if (viewState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun EventFilterSheet(
    onDismiss: () -> Unit,
    onApplyFilters: (List<String>, Double, Double?) -> Unit
) {
    // TODO: Implement filter sheet UI
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Filters", style = MaterialTheme.typography.headlineSmall)
            // Filter controls will be implemented here
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onDismiss() }) {
                    Text("Apply")
                }
            }
        }
    }
}
