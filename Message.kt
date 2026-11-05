package com.example.simbachat.dashboard

import com.google.firebase.firestore.PropertyName

data class Message(

    val sender_Id: String = "",

    val receiver_Id: String = "",

    @get:PropertyName("message")
    @set:PropertyName("message")
    var Message: String = "",

    val timestamp: Long = 0
)