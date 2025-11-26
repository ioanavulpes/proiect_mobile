package com.localpulse.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

/**
 * Service for geocoding city names to coordinates
 */
class GeocodingService(private val context: Context) {
    
    private val geocoder = Geocoder(context)
    private val cache = mutableMapOf<String, LatLng?>()
    
    /**
     * Convert city name to coordinates
     * Returns null if geocoding fails
     */
    suspend fun getCoordinates(cityName: String): LatLng? = withContext(Dispatchers.IO) {
        // Check cache first
        if (cache.containsKey(cityName)) {
            return@withContext cache[cityName]
        }
        
        try {
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Use new async API for Android 13+
                getAddressAsync(cityName)
            } else {
                // Use legacy synchronous API
                @Suppress("DEPRECATION")
                geocoder.getFromLocationName(cityName, 1)?.firstOrNull()
            }
            
            val latLng = result?.let { 
                LatLng(it.latitude, it.longitude) 
            }
            
            // Cache result
            cache[cityName] = latLng
            latLng
        } catch (e: Exception) {
            android.util.Log.e("GeocodingService", "Error geocoding $cityName: ${e.message}", e)
            null
        }
    }
    
    /**
     * Get address asynchronously (Android 13+)
     */
    private suspend fun getAddressAsync(cityName: String): Address? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { continuation ->
                geocoder.getFromLocationName(cityName, 1) { addresses ->
                    continuation.resume(addresses.firstOrNull())
                }
            }
        } else {
            null
        }
    }
    
    /**
     * Clear the cache
     */
    fun clearCache() {
        cache.clear()
    }
}

