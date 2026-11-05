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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simbachat.R
import com.example.simbachat.auth.ChatViewModel
import com.google.firebase.auth.FirebaseAuth

class Chat : AppCompatActivity() {

    private lateinit var messageAdapter: MessageAdapter
    private val messageList = ArrayList<Message>()
    private lateinit var receiverId: String
    private lateinit var chatViewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        val mainView = findViewById<View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
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
            Toast.makeText(this, "Please sign in first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        receiverId = intent.getStringExtra("receiverId") ?: ""
        if (receiverId.isEmpty()) {
            Toast.makeText(this, "No chat user selected", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val etMessage = findViewById<EditText>(R.id.etMessage)
        val sendButton = findViewById<ImageButton>(R.id.btnSend)
        val rvMessages = findViewById<RecyclerView>(R.id.rvMessages)
        val etReceiver = findViewById<EditText>(R.id.etReceiver)
        etReceiver?.visibility = View.GONE

        /*
         * Back button
         */
        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        /*
         * RecyclerView Setup
         */
        messageAdapter = MessageAdapter(messageList)
        rvMessages.layoutManager = LinearLayoutManager(this)
        rvMessages.adapter = messageAdapter

        /*
         * Initialize ViewModel
         */
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        /*
         * Generate Chat ID and start listening
         */
        val ids = listOf(currentUser.uid, receiverId).sorted()
        val chatId = "${ids[0]}_${ids[1]}"

        chatViewModel.startListeningToChat(chatId)

        /*
         * Observe Messages from ViewModel
         */
        chatViewModel.messages.observe(this) { newMessages ->
            messageList.clear()
            messageList.addAll(newMessages)
            messageAdapter.updateMessages(messageList)

            if (messageList.isNotEmpty()) {
                rvMessages.scrollToPosition(messageList.size - 1)
            }
        }

        /*
         * Observe Send Result
         */
        chatViewModel.sendSuccess.observe(this) { success ->
            if (!success) {
                Toast.makeText(this, "Message Not Sent", Toast.LENGTH_SHORT).show()
            }
        }

        /*
         * SEND MESSAGE
         */
        sendButton.setOnClickListener {
            val messageText = etMessage.text.toString().trim()
            if (messageText.isEmpty()) {
                return@setOnClickListener
            }

            chatViewModel.sendMessage(
                chatRoomId = chatId,
                senderId = currentUser.uid,
                receiverId = receiverId,
                messageText = messageText
            )

            etMessage.text.clear()
        }
    }
}
