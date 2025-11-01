package com.localpulse.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.localpulse.MainActivity
import com.localpulse.R

class PulseFCMService : FirebaseMessagingService() {
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        // Handle data payload
        remoteMessage.data.isNotEmpty().let {
            // Process data payload
        }
        
        // Handle notification payload
        remoteMessage.notification?.let {
            sendNotification(
                title = it.title ?: "LocalPulse",
                body = it.body ?: "You have a new notification"
            )
        }
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send token to server
        sendTokenToServer(token)
    }
    
    private fun sendNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val channelId = "pulse_notifications"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "LocalPulse Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for events, messages, and updates"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        notificationManager.notify(0, notificationBuilder.build())
    }
    
    private fun sendTokenToServer(token: String) {
        // TODO: Send FCM token to server
    }
}
