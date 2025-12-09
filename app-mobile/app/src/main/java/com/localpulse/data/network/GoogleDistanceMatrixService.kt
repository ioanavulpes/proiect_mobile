package com.localpulse.data.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/**
 * Service for calculating real travel distances using Google Distance Matrix API
 */
class GoogleDistanceMatrixService(
    private val apiKey: String
) {
    private val client = OkHttpClient()
    private val json = Json { 
        ignoreUnknownKeys = true
        isLenient = true
    }

    companion object {
        private const val TAG = "DistanceMatrixAPI"
        private const val BASE_URL = "https://maps.googleapis.com/maps/api/distancematrix/json"
    }


    suspend fun getDistanceAndDuration(
        originLat: Double,
        originLng: Double,
        destLat: Double,
        destLng: Double,
        mode: String = "driving"
    ): DistanceMatrixResult? {
        val origin = "$originLat,$originLng"
        val destination = "$destLat,$destLng"
        
        val url = "$BASE_URL?origins=$origin&destinations=$destination&mode=$mode&key=$apiKey"
        
        Log.d(TAG, "========================================")
        Log.d(TAG, "🌐 DISTANCE MATRIX API REQUEST")
        Log.d(TAG, "========================================")
        Log.d(TAG, "Origin: $origin")
        Log.d(TAG, "Destination: $destination")
        Log.d(TAG, "Mode: $mode")
        Log.d(TAG, "API Key: ${apiKey.take(10)}...${apiKey.takeLast(5)}")
        Log.d(TAG, "Full URL: ${url.replace(apiKey, "***API_KEY***")}")

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return try {
            // Execute network call on IO thread to avoid NetworkOnMainThreadException
            val (response, responseBody) = withContext(Dispatchers.IO) {
                val resp = client.newCall(request).execute()
                val body = resp.body?.string()
                Pair(resp, body)
            }

            Log.d(TAG, " Response Code: ${response.code}")
            Log.d(TAG, " Response Message: ${response.message}")

            if (!response.isSuccessful) {
                Log.e(TAG, "========================================")
                Log.e(TAG, " API REQUEST FAILED")
                Log.e(TAG, "========================================")
                Log.e(TAG, "Status Code: ${response.code}")
                Log.e(TAG, "Error Body: $responseBody")
                
                if (response.code == 403) {
                    Log.e(TAG, " ERROR 403: Distance Matrix API is probably NOT ENABLED or API key is invalid!")
                    Log.e(TAG, " Please enable it at: https://console.cloud.google.com/apis/library/distance-matrix-backend.googleapis.com")
                }
                
                return null
            }

            if (responseBody == null) {
                Log.e(TAG, " Empty response body")
                return null
            }

            Log.d(TAG, "📄 Response body (first 1000 chars): ${responseBody.take(1000)}")

            val apiResponse = json.decodeFromString<DistanceMatrixResponse>(responseBody)

            Log.d(TAG, " API Status: ${apiResponse.status}")
            
            if (apiResponse.status != "OK") {
                Log.e(TAG, "========================================")
                Log.e(TAG, " API STATUS NOT OK")
                Log.e(TAG, "========================================")
                Log.e(TAG, "Status: ${apiResponse.status}")
                Log.e(TAG, "Error Message: ${apiResponse.errorMessage}")
                
                when (apiResponse.status) {
                    "REQUEST_DENIED" -> {
                        Log.e(TAG, " REQUEST_DENIED: Distance Matrix API is not enabled or API key restrictions are too strict")
                        Log.e(TAG, " Solution: Enable API and check key restrictions in Google Cloud Console")
                    }
                    "OVER_QUERY_LIMIT" -> {
                        Log.e(TAG, " OVER_QUERY_LIMIT: You've exceeded your quota")
                    }
                    "INVALID_REQUEST" -> {
                        Log.e(TAG, " INVALID_REQUEST: The request is malformed")
                    }
                }
                
                return null
            }

            val row = apiResponse.rows?.firstOrNull()
            val element = row?.elements?.firstOrNull()

            if (element == null) {
                Log.e(TAG, " No distance data in response")
                Log.e(TAG, "Rows: ${apiResponse.rows}")
                return null
            }

            Log.d(TAG, " Element Status: ${element.status}")

            if (element.status != "OK") {
                Log.e(TAG, "========================================")
                Log.e(TAG, " ELEMENT STATUS NOT OK")
                Log.e(TAG, "========================================")
                Log.e(TAG, "Element Status: ${element.status}")
                
                when (element.status) {
                    "ZERO_RESULTS" -> {
                        Log.e(TAG, " ZERO_RESULTS: No route found between origin and destination")
                        Log.e(TAG, " This can happen if the locations are too far apart or unreachable")
                    }
                    "NOT_FOUND" -> {
                        Log.e(TAG, " NOT_FOUND: One of the locations could not be geocoded")
                    }
                }
                
                return null
            }

            val distanceMeters = element.distance?.value
            val durationSeconds = element.duration?.value

            if (distanceMeters == null || durationSeconds == null) {
                Log.e(TAG, " Missing distance or duration values")
                Log.e(TAG, "Distance: ${element.distance}")
                Log.e(TAG, "Duration: ${element.duration}")
                return null
            }

            val result = DistanceMatrixResult(
                distanceMeters = distanceMeters,
                distanceText = element.distance.text ?: "${distanceMeters / 1000.0} km",
                durationSeconds = durationSeconds,
                durationText = element.duration?.text ?: "${durationSeconds / 60} min"
            )

            Log.d(TAG, "========================================")
            Log.d(TAG, " SUCCESS!")
            Log.d(TAG, "========================================")
            Log.d(TAG, "Distance: ${result.distanceText} (${result.distanceKm} km)")
            Log.d(TAG, "Duration: ${result.durationText} (${result.durationMinutes} min)")
            Log.d(TAG, "========================================")

            return result

        } catch (e: IOException) {
            Log.e(TAG, "========================================")
            Log.e(TAG, " NETWORK ERROR")
            Log.e(TAG, "========================================")
            Log.e(TAG, "Message: ${e.message}", e)
            Log.e(TAG, " Check your internet connection")
            null
        } catch (e: Exception) {
            Log.e(TAG, "========================================")
            Log.e(TAG, " ERROR PARSING RESPONSE")
            Log.e(TAG, "========================================")
            Log.e(TAG, "Message: ${e.message}", e)
            Log.e(TAG, "This might be a JSON parsing error")
            null
        }
    }
}

/**
 * Result from Distance Matrix API
 */
data class DistanceMatrixResult(
    val distanceMeters: Int,
    val distanceText: String,
    val durationSeconds: Int,
    val durationText: String
) {
    val distanceKm: Double
        get() = distanceMeters / 1000.0
    
    val durationMinutes: Int
        get() = durationSeconds / 60
}

/**
 * Distance Matrix API Response Models
 */
@Serializable
data class DistanceMatrixResponse(
    val status: String,
    @SerialName("origin_addresses")
    val originAddresses: List<String>? = null,
    @SerialName("destination_addresses")
    val destinationAddresses: List<String>? = null,
    val rows: List<DistanceMatrixRow>? = null,
    @SerialName("error_message")
    val errorMessage: String? = null
)

@Serializable
data class DistanceMatrixRow(
    val elements: List<DistanceMatrixElement>? = null
)

@Serializable
data class DistanceMatrixElement(
    val status: String,
    val distance: DistanceInfo? = null,
    val duration: DurationInfo? = null,
    @SerialName("duration_in_traffic")
    val durationInTraffic: DurationInfo? = null
)

@Serializable
data class DistanceInfo(
    val value: Int? = null,  // Distance in meters
    val text: String? = null // Formatted distance string
)

@Serializable
data class DurationInfo(
    val value: Int? = null,  // Duration in seconds
    val text: String? = null // Formatted duration string
)

