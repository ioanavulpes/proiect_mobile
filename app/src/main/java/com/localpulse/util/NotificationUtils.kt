package com.localpulse.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.localpulse.R

object NotificationUtils {
    
    const val CHANNEL_EVENTS = "events"
    const val CHANNEL_MESSAGES = "messages"
    const val CHANNEL_ACHIEVEMENTS = "achievements"
    const val CHANNEL_GENERAL = "general"
    
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_EVENTS,
                    "Event Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications about events, reminders, and nearby activities"
                    enableLights(true)
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_MESSAGES,
                    "Chat Messages",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notifications for new chat messages and community updates"
                    enableLights(true)
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_ACHIEVEMENTS,
                    "Achievements",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Notifications for badges, points, and achievements"
                    enableLights(false)
                    enableVibration(false)
                },
                NotificationChannel(
                    CHANNEL_GENERAL,
                    "General",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "General app notifications and updates"
                    enableLights(true)
                    enableVibration(true)
                }
            )
            
            channels.forEach { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
    
    fun showNotification(
        context: Context,
        channelId: String,
        title: String,
        content: String,
        notificationId: Int = 0
    ) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, notification)
        }
    }
}
