package com.example.simbachat.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simbachat.dashboard.ChatRepository
import com.example.simbachat.dashboard.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ChatViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val chatRepository = ChatRepository()

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private val _sendSuccess = MutableLiveData<Boolean>()
    val sendSuccess: LiveData<Boolean> = _sendSuccess

    private var listenerRegistration: ListenerRegistration? = null

    fun startListeningToChat(chatRoomId: String) {
        listenerRegistration?.remove()
        listenerRegistration = db.collection("chats")
            .document(chatRoomId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("Listen failed: $e")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val messageList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Message::class.java)
                    }
                    _messages.value = messageList
                }
            }
    }

    fun sendMessage(chatRoomId: String, senderId: String, receiverId: String, messageText: String) {
        chatRepository.sendMessage(
            chatRoomId = chatRoomId,
            senderId = senderId,
            receiverId = receiverId,
            messageText = messageText,
            onSuccess = {
                _sendSuccess.value = true
            },
            onFailure = {
                _sendSuccess.value = false
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
