package com.example.simbachat.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simbachat.R
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(
    private var messageList: List<Message>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    private val currentUserId =
        FirebaseAuth.getInstance().currentUser?.uid

    override fun getItemViewType(position: Int): Int {

        val message =
            messageList[position]

        return if (
            message.sender_Id == currentUserId
        ) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        return if (
            viewType == VIEW_TYPE_SENT
        ) {

            val view =
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_message_sent,
                        parent,
                        false
                    )

            SentMessageViewHolder(view)

        } else {

            val view =
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_message_received,
                        parent,
                        false
                    )

            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {

        val message =
            messageList[position]

        val timeString =
            formatTime(message.timestamp)

        if (holder is SentMessageViewHolder) {

            holder.tvMessage.text =
                message.Message

            holder.tvTimestamp.text =
                timeString

        } else if (
            holder is ReceivedMessageViewHolder
        ) {

            holder.tvMessage.text =
                message.Message

            holder.tvTimestamp.text =
                timeString

            holder.tvAvatarInitials.text =
                if (message.sender_Id.isNotEmpty()) {
                    message.sender_Id
                        .take(1)
                        .uppercase()
                } else {
                    "U"
                }

            holder.tvSenderName.text =
                "User (${message.sender_Id.take(5)})"
        }
    }

    override fun getItemCount(): Int =
        messageList.size

    fun updateMessages(
        newMessages: List<Message>
    ) {

        messageList =
            newMessages

        notifyDataSetChanged()
    }

    private fun formatTime(
        timestamp: Long
    ): String {

        return try {

            val sdf =
                SimpleDateFormat(
                    "HH:mm",
                    Locale.getDefault()
                )

            sdf.format(
                Date(timestamp)
            )

        } catch (e: Exception) {

            ""
        }
    }

    class SentMessageViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvMessage: TextView =
            itemView.findViewById(
                R.id.tvMessage
            )

        val tvTimestamp: TextView =
            itemView.findViewById(
                R.id.tvTimestamp
            )
    }

    class ReceivedMessageViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvMessage: TextView =
            itemView.findViewById(
                R.id.tvMessage
            )

        val tvTimestamp: TextView =
            itemView.findViewById(
                R.id.tvTimestamp
            )

        val tvSenderName: TextView =
            itemView.findViewById(
                R.id.tvSenderName
            )

        val tvAvatarInitials: TextView =
            itemView.findViewById(
                R.id.tvAvatarInitials
            )
    }
}