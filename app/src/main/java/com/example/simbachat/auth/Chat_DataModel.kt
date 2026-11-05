package com.example.simbachat.auth

data class Chat_DataModel(
    val messageID: String = "",
    val senderID: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
