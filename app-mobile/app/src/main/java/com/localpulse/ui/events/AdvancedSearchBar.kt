package com.localpulse.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.localpulse.util.Constants

/**
 * Advanced search bar with filters for location, date, category and keyword
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSearchBar(
    onSearch: (city: String, keyword: String, category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCity by remember { mutableStateOf(Constants.DEFAULT_CITY) }
    var searchKeyword by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    
    var showCityDropdown by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Row 1: Location and Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Location TextField with Suggestions
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = selectedCity,
                        onValueChange = { selectedCity = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Location") },
                        placeholder = { Text("City name") },
                        leadingIcon = {
                            Icon(Icons.Default.Place, contentDescription = null)
                        },
                        trailingIcon = {
                            Row {
                                if (selectedCity.isNotEmpty()) {
                                    IconButton(
                                        onClick = { selectedCity = "" },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { showCityDropdown = !showCityDropdown },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "Show suggestions",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    
                    // Suggestions Dropdown
                    DropdownMenu(
                        expanded = showCityDropdown,
                        onDismissRequest = { showCityDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.45f)
                    ) {
                        // Filter cities based on input
                        val filteredCities = if (selectedCity.isBlank()) {
                            Constants.POPULAR_CITIES
                        } else {
                            Constants.POPULAR_CITIES.filter { 
                                it.contains(selectedCity, ignoreCase = true) 
                            }
                        }
                        
                        if (filteredCities.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No suggestions", style = MaterialTheme.typography.bodySmall) },
                                onClick = { },
                                enabled = false
                            )
                        } else {
                            filteredCities.forEach { city ->
                                DropdownMenuItem(
                                    text = { Text(city) },
                                    onClick = {
                                        selectedCity = city
                                        showCityDropdown = false
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Place, contentDescription = null)
                                    }
                                )
                            }
                        }
                    }
                }

                // Category Dropdown
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { showCategoryDropdown = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "CATEGORY",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = Constants.EVENT_CATEGORIES.find { it.second == selectedCategory }?.first 
                                        ?: "All Categories",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Select category",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    
                    DropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.45f)
                    ) {
                        Constants.EVENT_CATEGORIES.forEach { (name, value) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    selectedCategory = value
                                    showCategoryDropdown = false
                                },
                                leadingIcon = {
                                    val icon = when (value) {
                                        "music" -> Icons.Default.MusicNote
                                        "sports" -> Icons.Default.SportsBasketball
                                        "arts" -> Icons.Default.Palette
                                        "film" -> Icons.Default.Movie
                                        "family" -> Icons.Default.FamilyRestroom
                                        else -> Icons.Default.Category
                                    }
                                    Icon(icon, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 2: Search Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchKeyword,
                    onValueChange = { searchKeyword = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Artist, Event or Venue") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Button(
                    onClick = {
                        onSearch(selectedCity, searchKeyword, selectedCategory)
                    },
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Search", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

