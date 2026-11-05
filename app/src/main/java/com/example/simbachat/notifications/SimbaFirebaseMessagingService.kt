
package com.example.simbachat.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.simbachat.R
import com.example.simbachat.dashboard.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class SimbaFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID = "simbachat_messages"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        saveToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data

        val senderId = data["senderId"] ?: return
        val senderName = data["senderName"] ?: "New message"
        val chatId = data["chatId"] ?: return
        val messageText = data["message"] ?: "You received a new message."

        showMessageNotification(
            senderId = senderId,
            senderName = senderName,
            chatId = chatId,
            messageText = messageText
        )
    }

    private fun saveToken(token: String) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: return

        val tokenData = hashMapOf(
            "fcmToken" to token
        )

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .set(tokenData, com.google.firebase.firestore.SetOptions.merge())
    }

    private fun showMessageNotification(
        senderId: String,
        senderName: String,
        chatId: String,
        messageText: String
    ) {

        createNotificationChannel()

        val intent = Intent(this, Chat::class.java).apply {
            putExtra("receiverId", senderId)
            putExtra("chatId", chatId)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            chatId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(senderName)
            .setContentText(messageText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(messageText)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(this)
                .notify(chatId.hashCode(), notification)
        } catch (e: SecurityException) {
            // Notification permission has not been granted.
        }
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "SimbaChat Messages",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new SimbaChat messages"
            }

            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }
}

