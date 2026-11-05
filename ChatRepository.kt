package com.example.simbachat.dashboard

import com.google.firebase.firestore.FirebaseFirestore

class ChatRepository {
    private val db = FirebaseFirestore.getInstance()

    fun sendMessage(
        chatRoomId: String,
        senderId: String,
        receiverId: String,
        messageText: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val messageObj = Message(
            sender_Id = senderId,
            receiver_Id = receiverId,
            Message = messageText,
            timestamp = System.currentTimeMillis()
        )

        db.collection("chats")
            .document(chatRoomId)
            .collection("messages")
            .add(messageObj)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                android.util.Log.e(
                    "CHAT_SEND_ERROR",
                    "Failed to send message",
                    e
                )
                onFailure(e)
            }
    }
}
