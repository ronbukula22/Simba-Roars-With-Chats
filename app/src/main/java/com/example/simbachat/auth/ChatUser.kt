package com.example.simbachat.auth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ChatUser(
@DocumentId val uid: String = "",
    val name: String = "",
    val phone_number: String = "",
    val photo_Url: String = "",
    val status: String? = "Hi there! I'm roaring with chats",
    @ServerTimestamp val createdAt: Date? = null,
    @ServerTimestamp val lastSeen: Date? = null

)
