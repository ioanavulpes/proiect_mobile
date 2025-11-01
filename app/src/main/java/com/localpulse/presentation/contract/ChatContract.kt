package com.localpulse.presentation.contract

import com.localpulse.data.model.ChatGroup
import kotlinx.coroutines.flow.StateFlow

interface ChatContract {
    data class ViewState(
        val chatGroups: List<ChatGroup> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )
    
    interface View {
        val viewState: StateFlow<ViewState>
        fun loadChatGroups()
        fun createNewGroup()
        fun onGroupClick(groupId: String)
    }
}
