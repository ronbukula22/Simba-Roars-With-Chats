package com.example.simbachat.dashboard

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simbachat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class Chat : AppCompatActivity() {

    private lateinit var messageAdapter: MessageAdapter

    private val messageList = ArrayList<Message>()

    private var currentChatId: String? = null

    private var registration: ListenerRegistration? = null

    private lateinit var receiverId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_chat)

        val mainView = findViewById<View>(R.id.main)

        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, insets ->

                val systemBars =
                    insets.getInsets(WindowInsetsCompat.Type.systemBars())

                view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
                )

                insets
            }
        }

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            Toast.makeText(
                this,
                "Please sign in first",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        /*
         * Get the receiver UID from the Chats screen.
         */
        receiverId =
            intent.getStringExtra("receiverId") ?: ""

        if (receiverId.isEmpty()) {
            Toast.makeText(
                this,
                "No chat user selected",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        val etMessage =
            findViewById<EditText>(R.id.etMessage)

        val sendButton =
            findViewById<ImageButton>(R.id.btnSend)

        val rvMessages =
            findViewById<RecyclerView>(R.id.rvMessages)


        val etReceiver =
            findViewById<EditText>(R.id.etReceiver)

        etReceiver.visibility = View.GONE

        /*
         * Back button
         */
        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        /*
         * RecyclerView
         */
        messageAdapter =
            MessageAdapter(messageList)

        rvMessages.layoutManager =
            LinearLayoutManager(this)

        rvMessages.adapter =
            messageAdapter

        /*
         * Start listening for messages
         */
        setupMessageListener(
            currentUser.uid,
            receiverId
        )

        /*
         * SEND MESSAGE
         */
        sendButton.setOnClickListener {

            val messageText =
                etMessage.text
                    .toString()
                    .trim()

            if (messageText.isEmpty()) {
                return@setOnClickListener
            }

            val db =
                FirebaseFirestore.getInstance()

            val senderId =
                currentUser.uid

            val ids =
                listOf(
                    senderId,
                    receiverId
                ).sorted()

            val chatId =
                "${ids[0]}_${ids[1]}"

            val messageObj =
                Message(
                    sender_Id = senderId,
                    receiver_Id = receiverId,
                    Message = messageText,
                    timestamp = System.currentTimeMillis()
                )

            db.collection("chats")
                .document(chatId)
                .collection("Messages")
                .add(messageObj)
                .addOnSuccessListener {

                    etMessage.text.clear()

                    if (messageList.isNotEmpty()) {
                        rvMessages.scrollToPosition(
                            messageList.size - 1
                        )
                    }
                }
                .addOnFailureListener {

                    Toast.makeText(
                        this,
                        "Message Not Sent",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun setupMessageListener(
        senderId: String,
        receiverId: String
    ) {

        val ids =
            listOf(
                senderId,
                receiverId
            ).sorted()

        val chatId =
            "${ids[0]}_${ids[1]}"

        if (chatId == currentChatId) {
            return
        }

        registration?.remove()

        currentChatId = chatId

        val db =
            FirebaseFirestore.getInstance()

        registration =
            db.collection("chats")
                .document(chatId)
                .collection("Messages")
                .orderBy(
                    "timestamp",
                    Query.Direction.ASCENDING
                )
                .addSnapshotListener { snapshots, error ->

                    if (error != null) {
                        Toast.makeText(
                            this,
                            "Unable to load messages",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@addSnapshotListener
                    }

                    if (snapshots != null) {

                        messageList.clear()

                        for (doc in snapshots.documents) {

                            val message =
                                doc.toObject(
                                    Message::class.java
                                )

                            if (message != null) {
                                messageList.add(message)
                            }
                        }

                        messageAdapter.updateMessages(
                            messageList
                        )

                        val rvMessages =
                            findViewById<RecyclerView>(
                                R.id.rvMessages
                            )

                        if (messageList.isNotEmpty()) {

                            rvMessages.scrollToPosition(
                                messageList.size - 1
                            )
                        }
                    }
                }
    }

    override fun onDestroy() {

        super.onDestroy()

        registration?.remove()
    }
}