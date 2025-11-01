package com.localpulse.data.model

import java.util.Date

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val city: String = "",
    val preferences: UserPreferences = UserPreferences(),
    val profile: UserProfile = UserProfile(),
    val createdAt: Date = Date(),
    val lastActiveAt: Date = Date(),
    val isActive: Boolean = true
)

data class UserPreferences(
    val categories: List<EventCategory> = emptyList(),
    val maxDistance: Double = 10.0, // km
    val maxPrice: Double = 100.0,
    val timeSlots: List<TimeSlot> = emptyList(),
    val notifications: NotificationPreferences = NotificationPreferences(),
    val privacy: PrivacySettings = PrivacySettings()
)

data class UserProfile(
    val points: Int = 0,
    val level: Int = 1,
    val badges: List<Badge> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val eventsAttended: Int = 0,
    val eventsCreated: Int = 0,
    val streak: Int = 0
)

data class Badge(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val earnedAt: Date = Date(),
    val rarity: BadgeRarity = BadgeRarity.COMMON
)

data class Achievement(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val progress: Int = 0,
    val maxProgress: Int = 100,
    val isCompleted: Boolean = false,
    val completedAt: Date? = null
)

enum class BadgeRarity {
    COMMON,
    RARE,
    EPIC,
    LEGENDARY
}

enum class TimeSlot {
    MORNING,
    AFTERNOON,
    EVENING,
    NIGHT
}

data class NotificationPreferences(
    val pushEnabled: Boolean = true,
    val eventReminders: Boolean = true,
    val nearbyEvents: Boolean = true,
    val chatMessages: Boolean = true,
    val achievements: Boolean = true,
    val marketing: Boolean = false
)

data class PrivacySettings(
    val shareLocation: Boolean = true,
    val showProfile: Boolean = true,
    val allowChat: Boolean = true,
    val dataCollection: Boolean = true,
    val analytics: Boolean = true
)
