package com.localpulse.presentation.presenter

import com.localpulse.presentation.contract.ChatContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatPresenter : ChatContract.View {
    
    private val _viewState = MutableStateFlow(ChatContract.ViewState())
    override val viewState: StateFlow<ChatContract.ViewState> = _viewState.asStateFlow()
    
    override fun loadChatGroups() {
        _viewState.value = _viewState.value.copy(isLoading = true)
        // TODO: Load chat groups from repository
        _viewState.value = _viewState.value.copy(isLoading = false)
    }
    
    override fun createNewGroup() {
        // TODO: Navigate to create group screen
    }
    
    override fun onGroupClick(groupId: String) {
        // TODO: Navigate to chat screen
    }
}
