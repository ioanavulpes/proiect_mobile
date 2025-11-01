package com.localpulse.util

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.localpulse.data.model.Event
import com.localpulse.data.model.EventCategory
import com.localpulse.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Event title
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Event description
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Event details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category chip
                CategoryChip(category = event.category)
                
                // Price
                if (event.price > 0) {
                    Text(
                        text = "${event.price.toInt()} ${event.currency}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "Free",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Date and location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatEventDate(event.startDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = event.city,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: EventCategory,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, contentColor) = when (category) {
        EventCategory.MUSIC -> MusicColor to Color.White
        EventCategory.TECH -> TechColor to Color.White
        EventCategory.SPORTS -> SportsColor to Color.White
        EventCategory.FOOD -> FoodColor to Color.White
        EventCategory.ART -> ArtColor to Color.White
        EventCategory.EDUCATION -> EducationColor to Color.White
        EventCategory.BUSINESS -> BusinessColor to Color.White
        EventCategory.HEALTH -> HealthColor to Color.White
        EventCategory.FAMILY -> FamilyColor to Color.White
        EventCategory.NIGHTLIFE -> NightlifeColor to Color.White
        EventCategory.OTHER -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Text(
            text = getCategoryDisplayName(category),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}

private fun getCategoryDisplayName(category: EventCategory): String {
    return when (category) {
        EventCategory.MUSIC -> "Music"
        EventCategory.TECH -> "Tech"
        EventCategory.SPORTS -> "Sports"
        EventCategory.FOOD -> "Food"
        EventCategory.ART -> "Art"
        EventCategory.EDUCATION -> "Education"
        EventCategory.BUSINESS -> "Business"
        EventCategory.HEALTH -> "Health"
        EventCategory.FAMILY -> "Family"
        EventCategory.NIGHTLIFE -> "Nightlife"
        EventCategory.OTHER -> "Other"
    }
}

private fun formatEventDate(date: Date): String {
    val now = Date()
    val diff = date.time - now.time
    val days = diff / (24 * 60 * 60 * 1000)
    
    return when {
        days < 0 -> "Past"
        days == 0L -> "Today"
        days == 1L -> "Tomorrow"
        days < 7 -> "This week"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
    }
}
