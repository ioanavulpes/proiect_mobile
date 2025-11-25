package com.localpulse.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.localpulse.data.model.User
import com.localpulse.util.Constants
import com.localpulse.util.Resource
import kotlinx.coroutines.tasks.await

/**
 * Repository for handling authentication operations with Firebase Auth
 */
class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Get the current logged-in user
     */
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    /**
     * Check if user is logged in
     */
    fun isUserLoggedIn(): Boolean = currentUser != null

    /**
     * Login with email and password
     */
    suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let {
                Resource.Success(it)
            } ?: Resource.Error("Login failed: User is null")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed")
        }
    }

    /**
     * Register a new user with email and password
     */
    suspend fun register(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                // Create user document in Firestore
                val user = User(
                    uid = firebaseUser.uid,
                    email = email,
                    createdAt = Timestamp.now()
                )
                
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(firebaseUser.uid)
                    .set(user)
                    .await()
                
                Resource.Success(firebaseUser)
            } ?: Resource.Error("Registration failed: User is null")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed")
        }
    }

    /**
     * Logout the current user
     */
    fun logout() {
        auth.signOut()
    }
}

