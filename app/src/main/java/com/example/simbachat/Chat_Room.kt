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
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Chat_Room : AppCompatActivity() {

    private lateinit var liveRoomAdapter:
            LiveRoomMessageAdapter

    private val liveMessagesList =
        ArrayList<Message>()

    private val currentRoomId =
        "joburg_vibes_room"


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        enableEdgeToEdge()

        setContentView(
            R.layout.activity_chat_room
        )


        val mainView =
            findViewById<View>(
                R.id.main
            )

        if (mainView != null) {

            ViewCompat.setOnApplyWindowInsetsListener(
                mainView
            ) { view, insets ->

                val systemBars =
                    insets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                    )

                view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
                )

                insets
            }
        }


        findViewById<View>(
            R.id.btnBack
        )?.setOnClickListener {

            finish()
        }


        val rvMessages =
            findViewById<RecyclerView>(
                R.id.rvMessages
            )

        val etMessage =
            findViewById<EditText>(
                R.id.etMessage
            )

        val btnSend =
            findViewById<ImageButton>(
                R.id.btnSend
            )


        liveRoomAdapter =
            LiveRoomMessageAdapter(
                liveMessagesList
            )

        rvMessages.layoutManager =
            LinearLayoutManager(this)

        rvMessages.adapter =
            liveRoomAdapter


        val currentUser =
            FirebaseAuth
                .getInstance()
                .currentUser

        val db =
            FirebaseFirestore
                .getInstance()


        // SEND ROOM MESSAGE
        btnSend.setOnClickListener {

            val messageText =
                etMessage
                    .text
                    .toString()
                    .trim()


            if (
                messageText.isEmpty() ||
                currentUser == null
            ) {

                return@setOnClickListener
            }


            val msgObject =
                Message(
                    sender_Id =
                        currentUser.uid,

                    receiver_Id =
                        currentRoomId,

                    Message =
                        messageText,

                    timestamp =
                        System.currentTimeMillis()
                )


            db.collection("rooms")
                .document(currentRoomId)
                .collection("messages")
                .add(msgObject)
                .addOnSuccessListener {

                    etMessage.text.clear()
                }
                .addOnFailureListener {

                    Toast.makeText(
                        this,
                        "Failed to send message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }


        // REAL-TIME ROOM LISTENER
        db.collection("rooms")
            .document(currentRoomId)
            .collection("messages")
            .orderBy(
                "timestamp",
                Query.Direction.ASCENDING
            )
            .addSnapshotListener {
                    snapshots,
                    error ->

                if (error != null) {

                    return@addSnapshotListener
                }


                if (snapshots != null) {

                    liveMessagesList.clear()


                    for (
                    document in snapshots.documents
                    ) {

                        val messageObj =
                            document.toObject(
                                Message::class.java
                            )

                        if (
                            messageObj != null
                        ) {

                            liveMessagesList.add(
                                messageObj
                            )
                        }
                    }


                    liveRoomAdapter
                        .notifyDataSetChanged()


                    if (
                        liveMessagesList.isNotEmpty()
                    ) {

                        rvMessages.scrollToPosition(
                            liveMessagesList.size - 1
                        )
                    }
                }
            }
    }


    class LiveRoomMessageAdapter(
        private val messageList:
        List<Message>
    ) :
        RecyclerView.Adapter<
                LiveRoomMessageAdapter.ViewHolder
                >() {


        class ViewHolder(
            view: View
        ) :
            RecyclerView.ViewHolder(view) {

            val tvInitials:
                    TextView =
                view.findViewById(
                    R.id.tvAvatarInitials
                )

            val tvHandle:
                    TextView =
                view.findViewById(
                    R.id.tvSenderName
                )

            val tvText:
                    TextView =
                view.findViewById(
                    R.id.tvMessage
                )

            val tvTime:
                    TextView =
                view.findViewById(
                    R.id.tvTimestamp
                )
        }


        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {

            val view =
                LayoutInflater.from(
                    parent.context
                ).inflate(
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

            val item =
                messageList[position]


            holder.tvText.text =
                item.Message


            if (
                item.timestamp > 0
            ) {

                val sdf =
                    SimpleDateFormat(
                        "HH:mm",
                        Locale.getDefault()
                    )

                holder.tvTime.text =
                    sdf.format(
                        Date(item.timestamp)
                    )

            } else {

                holder.tvTime.text =
                    "10:00"
            }


            val shortId =
                if (
                    item.sender_Id.isNotEmpty()
                ) {

                    item.sender_Id
                        .take(4)
                        .uppercase()

                } else {

                    "5532"
                }


            when (position % 4) {

                0 -> {

                    holder.tvInitials.text =
                        "P"

                    holder.tvHandle.text =
                        "Pride #$shortId"
                }

                1 -> {

                    holder.tvInitials.text =
                        "M"

                    holder.tvHandle.text =
                        "Mane #$shortId"
                }

                2 -> {

                    holder.tvInitials.text =
                        "R"

                    holder.tvHandle.text =
                        "Roar #$shortId"
                }

                else -> {

                    holder.tvInitials.text =
                        "C"

                    holder.tvHandle.text =
                        "Cub #$shortId"
                }
            }
        }


        override fun getItemCount():
                Int =
            messageList.size
    }
}