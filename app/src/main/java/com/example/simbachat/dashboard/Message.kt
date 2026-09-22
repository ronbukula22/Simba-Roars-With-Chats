package com.example.simbachat.dashboard

data class Message(
    val sender_Id: String = "",
    val receiver_Id: String = "",
    val Message: String = "",
    val timestamp: Long = 0
)