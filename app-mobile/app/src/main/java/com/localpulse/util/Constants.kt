package com.localpulse.util

object Constants {
    // Ticketmaster API
    const val TICKETMASTER_BASE_URL = "https://app.ticketmaster.com/discovery/v2/"
    const val TICKETMASTER_EVENTS_ENDPOINT = "events.json"
    
    // Default search parameters
    const val DEFAULT_CITY = "London"  // Changed from Bucharest - Ticketmaster has limited coverage in Romania
    const val DEFAULT_PAGE_SIZE = 50
    
    // Event Categories
    val EVENT_CATEGORIES = listOf(
        "All Categories" to "",
        "Music" to "music",
        "Sports" to "sports",
        "Arts & Theatre" to "arts",
        "Film" to "film",
        "Family" to "family"
    )
    
    // Popular Cities (prioritized by Ticketmaster coverage)
    val POPULAR_CITIES = listOf(
        "All Cities",
        "London",
        "New York",
        "Los Angeles",
        "Paris",
        "Chicago",
        "Berlin",
        "Amsterdam",
        "Madrid",
        "Vienna",
        "Budapest",
        "Bucharest",  // Limited Ticketmaster coverage
        "Cluj-Napoca",
        "Timisoara"
    )
    
    // Firestore collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_FAVORITES = "favorites"
    
    // Navigation routes
    const val ROUTE_LOGIN = "login"
    const val ROUTE_REGISTER = "register"
    const val ROUTE_HOME = "home"
    const val ROUTE_EVENTS = "events"
    const val ROUTE_EVENT_DETAILS = "event_details/{eventId}"
    const val ROUTE_FAVORITES = "favorites"
    const val ROUTE_MAP = "map"
    const val ROUTE_RECOMMENDATIONS = "recommendations"
}

