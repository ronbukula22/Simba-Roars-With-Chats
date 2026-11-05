package com.example.simbachat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simbachat.dashboard.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Chat_Room : AppCompatActivity() {

    private lateinit var liveRoomAdapter: LiveRoomMessageAdapter
    private val liveMessagesList = ArrayList<Message>()
    private var currentRoomId: String = "joburg_vibes_room"
    private var roomListenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat_room)

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

        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please sign in first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Get dynamic room ID from Intent Extras if available
        currentRoomId = intent.getStringExtra("roomId") ?: "joburg_vibes_room"

        val rvMessages = findViewById<RecyclerView>(R.id.rvMessages)
        val etMessage = findViewById<EditText>(R.id.etMessage)
        val btnSend = findViewById<ImageButton>(R.id.btnSend)

        liveRoomAdapter = LiveRoomMessageAdapter(liveMessagesList)
        rvMessages.layoutManager = LinearLayoutManager(this)
        rvMessages.adapter = liveRoomAdapter

        val db = FirebaseFirestore.getInstance()


        btnSend.setOnClickListener {
            val messageText = etMessage.text.toString().trim()
            if (messageText.isEmpty()) {
                return@setOnClickListener
            }

            val msgObject = Message(
                sender_Id = currentUser.uid,
                receiver_Id = currentRoomId,
                Message = messageText,
                timestamp = System.currentTimeMillis()
            )

            db.collection("rooms")
                .document(currentRoomId)
                .collection("messages")
                .add(msgObject)
                .addOnSuccessListener {
                    etMessage.text.clear()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(
                        this,
                        "Failed: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    android.util.Log.e(
                        "ROOM_CHAT",
                        "Message sending failed",
                        error
                    )
                }
        }


        roomListenerRegistration = db.collection("rooms")
            .document(currentRoomId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    liveMessagesList.clear()
                    for (document in snapshots.documents) {
                        val messageObj = document.toObject(Message::class.java)
                        if (messageObj != null) {
                            liveMessagesList.add(messageObj)
                        }
                    }

                    liveRoomAdapter.notifyDataSetChanged()

                    if (liveMessagesList.isNotEmpty()) {
                        rvMessages.scrollToPosition(liveMessagesList.size - 1)
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        roomListenerRegistration?.remove()
    }



    class LiveRoomMessageAdapter(
        private val messageList: List<Message>
    ) : RecyclerView.Adapter<LiveRoomMessageAdapter.ViewHolder>() {

        private val nameCache = mutableMapOf<String, String>()

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvInitials: TextView =
                view.findViewById(R.id.tvAvatarInitials)

            val tvHandle: TextView =
                view.findViewById(R.id.tvSenderName)

            val tvText: TextView =
                view.findViewById(R.id.tvMessage)

            val tvTime: TextView =
                view.findViewById(R.id.tvTimestamp)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {

            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_room_message,
                parent,
                false
            )

            return ViewHolder(view)
        }

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
        ) {

            val item = messageList[position]

            holder.tvText.text = item.Message

            if (item.timestamp > 0) {
                val sdf = SimpleDateFormat(
                    "HH:mm",
                    Locale.getDefault()
                )

                holder.tvTime.text =
                    sdf.format(Date(item.timestamp))
            } else {
                holder.tvTime.text = ""
            }

            val senderId = item.sender_Id

            val cachedName = nameCache[senderId]

            if (cachedName != null) {

                displaySender(holder, cachedName)

            } else {

                holder.tvHandle.text = "Loading..."
                holder.tvInitials.text = "?"

                FirebaseFirestore.getInstance()
                    .collection("public_profiles")
                    .document(senderId)
                    .get()
                    .addOnSuccessListener { document ->

                        val profileName =
                            document.getString("name") ?: "Simba user"

                        nameCache[senderId] = profileName

                        displaySender(holder, profileName)
                    }
                    .addOnFailureListener {

                        nameCache[senderId] = "Simba user"

                        displaySender(holder, "Simba user")
                    }
            }
        }

        private fun displaySender(
            holder: ViewHolder,
            name: String
        ) {

            holder.tvHandle.text = name

            holder.tvInitials.text =
                name.firstOrNull()?.uppercase() ?: "S"
        }

        override fun getItemCount(): Int {
            return messageList.size
        }
    }


}
