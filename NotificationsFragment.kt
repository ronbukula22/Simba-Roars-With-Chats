package com.example.simbachat.util

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.simbachat.R

class NotificationsFragment : Fragment() {

    private var pendingRegion: String? = null

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            val region = pendingRegion
            pendingRegion = null

            if (granted && region != null) {
                showNotification(region)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Notification permission is needed to show alerts.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_notifications,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.saNewsNotification)
            .setOnClickListener {
                requestOrShowNotification("South Africa")
            }

        view.findViewById<View>(R.id.globalNewsNotification)
            .setOnClickListener {
                requestOrShowNotification("world")
            }
    }

    private fun requestOrShowNotification(region: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            val permission = Manifest.permission.POST_NOTIFICATIONS

            if (
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                pendingRegion = region
                notificationPermissionLauncher.launch(permission)
                return
            }
        }

        showNotification(region)
    }

    private fun showNotification(region: String) {
        val shown = NewsNotificationHelper.showTestNewsNotification(
            requireContext(),
            region
        )

        if (!isAdded) return

        val message = if (shown) {
            "Notification sent!"
        } else {
            "Couldn't show the notification. Check your notification settings."
        }

        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}