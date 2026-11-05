package com.example.simbachat.util

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.simbachat.Chat_Room
import com.example.simbachat.R

class RoomsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_rooms,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        val joburgVibesRoom =
            view.findViewById<View>(
                R.id.joburgVibesRoom
            )

        joburgVibesRoom.setOnClickListener {

            val intent =
                Intent(
                    requireContext(),
                    Chat_Room::class.java
                )

            startActivity(intent)
        }
    }
}