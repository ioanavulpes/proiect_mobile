package com.localpulse.data.repository

import com.localpulse.data.model.ChatGroup
import com.localpulse.data.model.Message
import com.localpulse.data.model.MessageType
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(
        chatId: String,
        content: String,
        type: MessageType = MessageType.TEXT,
        replyTo: String? = null
    ): Message
    
    suspend fun getMessages(chatId: String): Flow<List<Message>>
    suspend fun getChatGroups(userId: String): Flow<List<ChatGroup>>
    suspend fun createEventChat(eventId: String): String
    suspend fun joinGroup(groupId: String, userId: String)
    suspend fun leaveGroup(groupId: String, userId: String)
    suspend fun markAsRead(chatId: String, userId: String)
    suspend fun getUnreadCount(userId: String): Flow<Int>
}
