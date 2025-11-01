package com.localpulse.domain.usecase

import com.localpulse.data.model.Message
import com.localpulse.data.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        chatId: String,
        content: String,
        type: com.localpulse.data.model.MessageType = com.localpulse.data.model.MessageType.TEXT,
        replyTo: String? = null
    ): Result<Message> {
        return try {
            val message = chatRepository.sendMessage(
                chatId = chatId,
                content = content,
                type = type,
                replyTo = replyTo
            )
            Result.success(message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
