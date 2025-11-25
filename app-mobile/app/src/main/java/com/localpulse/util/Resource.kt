package com.localpulse.util

/**
 * A sealed class representing the state of a resource (API call, database query, etc.)
 * Used for handling loading, success, and error states in ViewModels
 */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}

