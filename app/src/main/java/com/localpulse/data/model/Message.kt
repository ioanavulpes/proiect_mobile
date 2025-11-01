package com.localpulse.data.model

import java.util.Date

data class Message(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderPhotoUrl: String = "",
    val content: String = "",
    val type: MessageType = MessageType.TEXT,
    val timestamp: Date = Date(),
    val isEdited: Boolean = false,
    val editedAt: Date? = null,
    val replyTo: String? = null,
    val attachments: List<Attachment> = emptyList()
)

data class ChatGroup(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val type: ChatType = ChatType.EVENT,
    val eventId: String? = null,
    val category: EventCategory? = null,
    val members: List<String> = emptyList(),
    val admins: List<String> = emptyList(),
    val createdAt: Date = Date(),
    val lastMessage: Message? = null,
    val isActive: Boolean = true
)

data class Attachment(
    val id: String = "",
    val type: AttachmentType = AttachmentType.IMAGE,
    val url: String = "",
    val name: String = "",
    val size: Long = 0
)

enum class MessageType {
    TEXT,
    IMAGE,
    FILE,
    LOCATION,
    SYSTEM
}

enum class ChatType {
    EVENT,
    GROUP,
    PRIVATE
}

enum class AttachmentType {
    IMAGE,
    VIDEO,
    AUDIO,
    DOCUMENT,
    LOCATION
}
