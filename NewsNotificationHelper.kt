package com.example.simbachat.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object NewsNotificationHelper {

    private const val CHANNEL_ID = "simba_news_updates"

    fun showTestNewsNotification(
        context: Context,
        region: String
    ): Boolean {

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        return try {
            createNotificationChannel(context)

            val isSouthAfrica = region == "South Africa"

            val title = if (isSouthAfrica) {
                "South African News"
            } else {
                "Global News"
            }

            val message = if (isSouthAfrica) {
                "Your South African news update is ready. Tap to open SimbaChat."
            } else {
                "Your global news update is ready. Tap to open SimbaChat."
            }

            val launchIntent =
                context.packageManager.getLaunchIntentForPackage(
                    context.packageName
                )

            val pendingIntent = launchIntent?.let {
                PendingIntent.getActivity(
                    context,
                    if (isSouthAfrica) 101 else 102,
                    it,
                    PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_IMMUTABLE
                )
            }

            val notification = NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(message)
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .apply {
                    pendingIntent?.let { setContentIntent(it) }
                }
                .build()

            NotificationManagerCompat.from(context).notify(
                if (isSouthAfrica) 1001 else 1002,
                notification
            )

            true
        } catch (_: SecurityException) {
            false
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Simba News Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "News notifications from SimbaChat"
            }

            val manager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }
}