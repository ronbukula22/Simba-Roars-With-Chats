package com.example.simbachat.util

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.simbachat.Chat_Room
import com.example.simbachat.R
import com.example.simbachat.dashboard.Chat

class FirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_first,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Temporary test receiver IDs.
        // Replace these with real Firebase UIDs later.
        val testReceiver1 = "TEST_USER_001"
        val testReceiver2 = "TEST_USER_002"
        val testReceiver3 = "TEST_USER_003"

        // Direct Message 1
        view.findViewById<View>(R.id.chatItem1)?.setOnClickListener {
            openPrivateChat(testReceiver1)
        }

        // Direct Message 2
        view.findViewById<View>(R.id.chatItem2)?.setOnClickListener {
            openPrivateChat(testReceiver2)
        }

        // Direct Message 3
        view.findViewById<View>(R.id.chatItem3)?.setOnClickListener {
            openPrivateChat(testReceiver3)
        }

        // Group Chat 1
        view.findViewById<View>(R.id.groupChatItem1)?.setOnClickListener {
            openRoomChat()
        }

        // Group Chat 2
        view.findViewById<View>(R.id.groupChatItem2)?.setOnClickListener {
            openRoomChat()
        }
    }

    private fun openPrivateChat(receiverId: String) {

        val intent =
            Intent(
                requireContext(),
                Chat::class.java
            )

        intent.putExtra(
            "receiverId",
            receiverId
        )

        startActivity(intent)
    }

    private fun openRoomChat() {

        val intent =
            Intent(
                requireContext(),
                Chat_Room::class.java
            )

        startActivity(intent)
    }
}