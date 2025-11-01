package com.localpulse.data.model

import java.util.Date

data class Notification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val body: String = "",
    val type: NotificationType = NotificationType.EVENT_REMINDER,
    val data: Map<String, String> = emptyMap(),
    val isRead: Boolean = false,
    val createdAt: Date = Date(),
    val scheduledFor: Date? = null,
    val sentAt: Date? = null
)

enum class NotificationType {
    EVENT_REMINDER,
    NEARBY_EVENT,
    CHAT_MESSAGE,
    ACHIEVEMENT,
    TICKET_CONFIRMATION,
    EVENT_CANCELLED,
    MARKETING
}
