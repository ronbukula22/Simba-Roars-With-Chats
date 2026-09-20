package com.example.simbachat.auth

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class User_Repo {

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val users = db.collection("users")

    val uid: String? get() = auth.currentUser?.uid

    suspend fun saveProfile(name: String, phone_number: String, status: String, photo_Url: String) {
        val user = auth.currentUser ?: error("Not signed in")
        val doc = users.document(user.uid)

        val data = mutableMapOf<String, Any?>(
            "uid" to user.uid,
            "name" to name,
            "phone_number" to phone_number,
            "status" to status,
            "photo_Url" to photo_Url,
            "createdAt" to FieldValue.serverTimestamp()
        )

        if (!doc.get().await().exists()) {
            data["createdAt"] = FieldValue.serverTimestamp()
        }

        doc.set(data, SetOptions.merge()).await()
    }

    suspend fun get_Profile(userId: String = uid!!): ChatUser? =
        users.document(userId).get().await().toObject(ChatUser::class.java)

    fun observeProfile(onChange: (ChatUser) -> Unit): ListenerRegistration? {
        val id = uid ?: return null
        return users.document(id).addSnapshotListener { snap, error ->
            if (error != null) return@addSnapshotListener
            snap?.toObject(ChatUser::class.java)?.let { onChange(it) }
        }
    }

    fun touchLastSeen() {
        val id = uid ?: return
        users.document(id).update("lastSeen", FieldValue.serverTimestamp())
    }
}
