package com.localpulse.util

object Constants {
    // Ticketmaster API
    const val TICKETMASTER_BASE_URL = "https://app.ticketmaster.com/discovery/v2/"
    const val TICKETMASTER_EVENTS_ENDPOINT = "events.json"
    
    // Default search parameters
    const val DEFAULT_CITY = "Bucharest"
    const val DEFAULT_PAGE_SIZE = 50
    
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

