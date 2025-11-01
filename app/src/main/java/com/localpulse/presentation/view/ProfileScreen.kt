package com.localpulse.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.localpulse.presentation.presenter.ProfilePresenter
import com.localpulse.presentation.contract.ProfileContract

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val presenter = remember { ProfilePresenter() }
    val viewState by presenter.viewState.collectAsState()
    
    LaunchedEffect(Unit) {
        presenter.loadUserProfile()
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Profile") },
            actions = {
                IconButton(onClick = { presenter.openSettings() }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ProfileHeader(
                        user = viewState.user,
                        onEditProfile = { presenter.editProfile() }
                    )
                }
                
                item {
                    StatsCard(
                        profile = viewState.user?.profile
                    )
                }
                
                item {
                    BadgesSection(
                        badges = viewState.user?.profile?.badges ?: emptyList()
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(
    user: com.localpulse.data.model.User?,
    onEditProfile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile picture placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user?.displayName?.firstOrNull()?.toString() ?: "?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = user?.displayName ?: "Guest User",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Text(
                text = user?.city ?: "Unknown City",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(onClick = onEditProfile) {
                Text("Edit Profile")
            }
        }
    }
}

@Composable
fun StatsCard(
    profile: com.localpulse.data.model.UserProfile?
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Points",
                    value = profile?.points?.toString() ?: "0"
                )
                StatItem(
                    label = "Level",
                    value = profile?.level?.toString() ?: "1"
                )
                StatItem(
                    label = "Events",
                    value = profile?.eventsAttended?.toString() ?: "0"
                )
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BadgesSection(
    badges: List<com.localpulse.data.model.Badge>
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Badges",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (badges.isEmpty()) {
                Text(
                    text = "No badges yet. Attend events to earn badges!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                // TODO: Implement badges grid
                Text(
                    text = "${badges.size} badges earned",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
