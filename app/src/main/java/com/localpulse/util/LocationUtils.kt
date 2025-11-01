package com.localpulse.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.localpulse.data.model.GeoPoint
import kotlinx.coroutines.tasks.await

object LocationUtils {
    
    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    suspend fun getCurrentLocation(context: Context): GeoPoint? {
        if (!hasLocationPermission(context)) {
            return null
        }
        
        return try {
            val fusedLocationClient: FusedLocationProviderClient = 
                LocationServices.getFusedLocationProviderClient(context)
            
            val location = fusedLocationClient.lastLocation.await()
            location?.let {
                GeoPoint(
                    latitude = it.latitude,
                    longitude = it.longitude
                )
            }
        } catch (e: Exception) {
            null
        }
    }
    
    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadius = 6371.0 // km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }
    
    fun formatDistance(distanceKm: Double): String {
        return when {
            distanceKm < 1 -> "${(distanceKm * 1000).toInt()}m"
            distanceKm < 10 -> String.format("%.1fkm", distanceKm)
            else -> "${distanceKm.toInt()}km"
        }
    }
}
