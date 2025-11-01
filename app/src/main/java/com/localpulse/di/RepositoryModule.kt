package com.localpulse.di

import com.localpulse.data.repository.ChatRepository
import com.localpulse.data.repository.EventRepository
import com.localpulse.data.repository.GamificationRepository
import com.localpulse.data.repository.TicketRepository
import com.localpulse.data.repository.UserBehaviorRepository
import com.localpulse.data.repository.firebase.FirebaseChatRepository
import com.localpulse.data.repository.firebase.FirebaseEventRepository
import com.localpulse.data.repository.firebase.FirebaseGamificationRepository
import com.localpulse.data.repository.firebase.FirebaseTicketRepository
import com.localpulse.data.repository.firebase.FirebaseUserBehaviorRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindEventRepository(
        firebaseEventRepository: FirebaseEventRepository
    ): EventRepository
    
    @Binds
    @Singleton
    abstract fun bindChatRepository(
        firebaseChatRepository: FirebaseChatRepository
    ): ChatRepository
    
    @Binds
    @Singleton
    abstract fun bindGamificationRepository(
        firebaseGamificationRepository: FirebaseGamificationRepository
    ): GamificationRepository
    
    @Binds
    @Singleton
    abstract fun bindTicketRepository(
        firebaseTicketRepository: FirebaseTicketRepository
    ): TicketRepository
    
    @Binds
    @Singleton
    abstract fun bindUserBehaviorRepository(
        firebaseUserBehaviorRepository: FirebaseUserBehaviorRepository
    ): UserBehaviorRepository
}
