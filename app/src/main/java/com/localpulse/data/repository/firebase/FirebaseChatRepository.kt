package com.localpulse.data.repository.firebase

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.localpulse.data.model.ChatGroup
import com.localpulse.data.model.Message
import com.localpulse.data.model.MessageType
import com.localpulse.data.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseChatRepository @Inject constructor(
    private val database: FirebaseDatabase
) : ChatRepository {
    
    override suspend fun sendMessage(
        chatId: String,
        content: String,
        type: MessageType,
        replyTo: String?
    ): Message {
        val messageRef = database.reference.child("messages").child(chatId).push()
        val message = Message(
            id = messageRef.key ?: "",
            chatId = chatId,
            content = content,
            type = type,
            replyTo = replyTo
        )
        
        messageRef.setValue(message).await()
        return message
    }
    
    override suspend fun getMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val messages = mutableListOf<Message>()
                for (child in snapshot.children) {
                    val message = child.getValue(Message::class.java)
                    if (message != null) {
                        messages.add(message)
                    }
                }
                trySend(messages.sortedBy { it.timestamp.time })
            }
            
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                trySend(emptyList())
            }
        }
        
        database.reference.child("messages").child(chatId)
            .addValueEventListener(listener)
        
        awaitClose {
            database.reference.child("messages").child(chatId)
                .removeEventListener(listener)
        }
    }
    
    override suspend fun getChatGroups(userId: String): Flow<List<ChatGroup>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val groups = mutableListOf<ChatGroup>()
                for (child in snapshot.children) {
                    val group = child.getValue(ChatGroup::class.java)
                    if (group != null && group.members.contains(userId)) {
                        groups.add(group)
                    }
                }
                trySend(groups)
            }
            
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                trySend(emptyList())
            }
        }
        
        database.reference.child("chatGroups")
            .addValueEventListener(listener)
        
        awaitClose {
            database.reference.child("chatGroups")
                .removeEventListener(listener)
        }
    }
    
    override suspend fun createEventChat(eventId: String): String {
        val chatRef = database.reference.child("chatGroups").push()
        val chatGroup = ChatGroup(
            id = chatRef.key ?: "",
            name = "Event Chat",
            type = com.localpulse.data.model.ChatType.EVENT,
            eventId = eventId
        )
        
        chatRef.setValue(chatGroup).await()
        return chatGroup.id
    }
    
    override suspend fun joinGroup(groupId: String, userId: String) {
        val groupRef = database.reference.child("chatGroups").child(groupId)
        val snapshot = groupRef.get().await()
        val group = snapshot.getValue(ChatGroup::class.java)
        
        if (group != null && !group.members.contains(userId)) {
            val updatedMembers = group.members + userId
            groupRef.child("members").setValue(updatedMembers).await()
        }
    }
    
    override suspend fun leaveGroup(groupId: String, userId: String) {
        val groupRef = database.reference.child("chatGroups").child(groupId)
        val snapshot = groupRef.get().await()
        val group = snapshot.getValue(ChatGroup::class.java)
        
        if (group != null && group.members.contains(userId)) {
            val updatedMembers = group.members.filter { it != userId }
            groupRef.child("members").setValue(updatedMembers).await()
        }
    }
    
    override suspend fun markAsRead(chatId: String, userId: String) {
        // Implementation for marking messages as read
        // This could involve updating a "readBy" field in messages
    }
    
    override suspend fun getUnreadCount(userId: String): Flow<Int> = callbackFlow {
        // Implementation for counting unread messages
        // This would involve tracking read status per user per chat
        trySend(0)
        awaitClose { }
    }
}
