package com.example.simbachat.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.simbachat.R
import com.example.simbachat.auth.SignInActivity
import com.example.simbachat.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var auth: FirebaseAuth

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                saveFcmToken()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Authentication.
        auth = FirebaseAuth.getInstance()

        // Prevent users who are not signed in from accessing the dashboard.
        if (auth.currentUser == null) {

            val intent = Intent(this, SignInActivity::class.java)

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
            return
        }

        // Load the dashboard only after authentication succeeds.
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestNotificationPermission()

        val navHostFragment =
            supportFragmentManager.findFragmentById(
                R.id.navHostFragment
            ) as NavHostFragment

        val navController = navHostFragment.navController

        // Open the Notifications screen when the bell is tapped.
        binding.notificationButton.setOnClickListener {
            if (navController.currentDestination?.id != R.id.nav_notifications) {
                navController.navigate(R.id.nav_notifications)
            }
        }

        // Open the local phone and SIM contacts screen.
        binding.addContactButton.setOnClickListener {
            if (navController.currentDestination?.id != R.id.nav_add_contacts) {
                navController.navigate(R.id.nav_add_contacts)
            }
        }

        // Existing bottom navigation.
        binding.bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_chats -> {
                    navController.navigate(R.id.nav_chats)
                    true
                }

                R.id.nav_rooms -> {
                    navController.navigate(R.id.nav_rooms)
                    true
                }

                R.id.nav_discover -> {
                    navController.navigate(R.id.nav_discover)
                    true
                }

                R.id.nav_news -> {
                    navController.navigate(R.id.nav_news)
                    true
                }

                R.id.nav_profile -> {
                    navController.navigate(R.id.nav_profile)
                    true
                }

                else -> false
            }
        }
    }

    private fun saveFcmToken() {

        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: return

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->

                val tokenData = hashMapOf(
                    "fcmToken" to token
                )

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .set(tokenData, SetOptions.merge())
            }
    }

    private fun requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            } else {
                saveFcmToken()
            }

        } else {
            saveFcmToken()
        }
    }


}