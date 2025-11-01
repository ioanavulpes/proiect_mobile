package com.localpulse.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.localpulse.presentation.presenter.ChatPresenter
import com.localpulse.presentation.contract.ChatContract

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    val presenter = remember { ChatPresenter() }
    val viewState by presenter.viewState.collectAsState()
    
    LaunchedEffect(Unit) {
        presenter.loadChatGroups()
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Community") },
            actions = {
                IconButton(onClick = { presenter.createNewGroup() }) {
                    Icon(Icons.Default.Add, contentDescription = "New Group")
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
                items(viewState.chatGroups) { group ->
                    ChatGroupCard(
                        group = group,
                        onClick = { presenter.onGroupClick(group.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ChatGroupCard(
    group: com.localpulse.data.model.ChatGroup,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = group.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${group.members.size} members",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
