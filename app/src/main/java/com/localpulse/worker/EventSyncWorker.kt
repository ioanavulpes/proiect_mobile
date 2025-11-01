package com.localpulse.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.localpulse.data.repository.EventRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class EventSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val eventRepository: EventRepository
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            // Sync events from all external APIs
            eventRepository.syncEvents()
            
            // Schedule next sync
            scheduleNextSync()
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    private suspend fun scheduleNextSync() {
        // Schedule next sync in 12 hours
        // This would be implemented with WorkManager's periodic work
    }
}
