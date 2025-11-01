package com.localpulse.presentation.contract

import com.localpulse.data.model.User
import kotlinx.coroutines.flow.StateFlow

interface ProfileContract {
    data class ViewState(
        val user: User? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
    
    interface View {
        val viewState: StateFlow<ViewState>
        fun loadUserProfile()
        fun editProfile()
        fun openSettings()
    }
}
