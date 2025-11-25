package com.localpulse.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.localpulse.data.model.Event
import com.localpulse.data.model.Favorite
import com.localpulse.util.Constants
import com.localpulse.util.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repository for handling favorites stored in Firestore
 */
class FavoritesRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private fun getCurrentUserId(): String? = auth.currentUser?.uid

    /**
     * Add an event to favorites
     */
    suspend fun addFavorite(event: Event): Resource<Unit> {
        val userId = getCurrentUserId() ?: return Resource.Error("User not logged in")

        return try {
            val favorite = Favorite(
                userId = userId,
                eventId = event.id,
                eventName = event.name,
                eventImage = event.image,
                eventUrl = event.url,
                timestamp = Timestamp.now()
            )

            firestore.collection(Constants.COLLECTION_FAVORITES)
                .add(favorite)
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add favorite")
        }
    }

    /**
     * Remove an event from favorites
     */
    suspend fun removeFavorite(eventId: String): Resource<Unit> {
        val userId = getCurrentUserId() ?: return Resource.Error("User not logged in")

        return try {
            val querySnapshot = firestore.collection(Constants.COLLECTION_FAVORITES)
                .whereEqualTo("userId", userId)
                .whereEqualTo("eventId", eventId)
                .get()
                .await()

            for (document in querySnapshot.documents) {
                document.reference.delete().await()
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove favorite")
        }
    }

    /**
     * Check if an event is in favorites
     */
    suspend fun isFavorite(eventId: String): Boolean {
        val userId = getCurrentUserId() ?: return false

        return try {
            val querySnapshot = firestore.collection(Constants.COLLECTION_FAVORITES)
                .whereEqualTo("userId", userId)
                .whereEqualTo("eventId", eventId)
                .get()
                .await()

            !querySnapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get all favorites for the current user with real-time updates
     */
    fun getFavorites(): Flow<Resource<List<Favorite>>> = callbackFlow {
        val userId = getCurrentUserId()
        
        if (userId == null) {
            trySend(Resource.Error("User not logged in"))
            close()
            return@callbackFlow
        }

        val subscription = firestore.collection(Constants.COLLECTION_FAVORITES)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Failed to fetch favorites"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val favorites = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Favorite::class.java)?.copy(id = doc.id)
                    }
                    trySend(Resource.Success(favorites))
                }
            }

        awaitClose { subscription.remove() }
    }
}

