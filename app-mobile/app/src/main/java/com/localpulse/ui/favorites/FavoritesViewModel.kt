package com.localpulse.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.localpulse.data.model.Favorite
import com.localpulse.data.repository.FavoritesRepository
import com.localpulse.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing favorites
 */
class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _favoritesState = MutableStateFlow<Resource<List<Favorite>>>(Resource.Loading)
    val favoritesState: StateFlow<Resource<List<Favorite>>> = _favoritesState.asStateFlow()

    init {
        loadFavorites()
    }

    /**
     * Load favorites from Firestore with real-time updates
     */
    private fun loadFavorites() {
        viewModelScope.launch {
            favoritesRepository.getFavorites().collect { resource ->
                _favoritesState.value = resource
            }
        }
    }

    /**
     * Remove a favorite
     */
    fun removeFavorite(eventId: String) {
        viewModelScope.launch {
            favoritesRepository.removeFavorite(eventId)
        }
    }
}

/**
 * Factory for creating FavoritesViewModel with dependencies
 */
class FavoritesViewModelFactory(
    private val favoritesRepository: FavoritesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(favoritesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

