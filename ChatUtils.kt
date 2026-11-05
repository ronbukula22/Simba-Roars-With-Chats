package com.example.simbachat.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ChatUtils {

    /**
     * Generates a deterministic 1-on-1 chat room ID from two user IDs.
     */
    fun generateChatRoomId(userId1: String, userId2: String): String {
        if (userId1.isBlank() || userId2.isBlank()) return ""
        val sorted = listOf(userId1.trim(), userId2.trim()).sorted()
        return "${sorted[0]}_${sorted[1]}"
    }

    /**
     * Validates whether a message text is non-empty and ready to be sent.
     */
    fun isMessageValid(messageText: String?): Boolean {
        return !messageText.isNullOrBlank()
    }

    /**
     * Formats a Unix timestamp into a readable HH:mm time string.
     */
    fun formatTimestamp(timestamp: Long): String {
        if (timestamp <= 0) return "10:00"
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            sdf.format(Date(timestamp))
        } catch (e: Exception) {
            "10:00"
        }
    }

    /**
     * Generates avatar initials for a given user name or sender ID.
     */
    fun getAvatarInitials(nameOrId: String?): String {
        if (nameOrId.isNullOrBlank()) return "U"
        val trimmed = nameOrId.trim()
        val parts = trimmed.split(" ").filter { it.isNotBlank() }
        return if (parts.size >= 2) {
            "${parts[0][0]}${parts[1][0]}".uppercase()
        } else {
            trimmed.take(1).uppercase()
        }
    }

    /**
     * Formats a room handle for group room messages based on position.
     */
    fun getRoomHandle(position: Int, senderId: String): String {
        val shortId = if (senderId.isNotBlank()) senderId.take(4).uppercase() else "5532"
        val title = when (position % 4) {
            0 -> "Pride"
            1 -> "Mane"
            2 -> "Roar"
            else -> "Cub"
        }
        return "$title #$shortId"
    }
}
