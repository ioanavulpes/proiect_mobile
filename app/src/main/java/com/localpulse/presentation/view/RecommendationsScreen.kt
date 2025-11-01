package com.localpulse.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.localpulse.presentation.presenter.RecommendationsPresenter
import com.localpulse.presentation.contract.RecommendationsContract
import com.localpulse.util.EventCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationsScreen() {
    val presenter = remember { RecommendationsPresenter() }
    val viewState by presenter.viewState.collectAsState()
    
    LaunchedEffect(Unit) {
        presenter.loadRecommendations()
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("For You") },
            actions = {
                IconButton(onClick = { presenter.refreshRecommendations() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        )
        
        if (viewState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewState.recommendations) { event ->
                    EventCard(
                        event = event,
                        onClick = { presenter.onEventClick(event.id) }
                    )
                }
            }
        }
    }
}
