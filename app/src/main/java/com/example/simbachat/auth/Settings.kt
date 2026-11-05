package com.example.simbachat.auth

import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simbachat.R
import android.widget.ImageButton
import android.widget.LinearLayout
import kotlin.jvm.java
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import com.example.simbachat.MainActivity
import com.example.simbachat.Profile_Setup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class Settings : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val preferences by lazy {
        getSharedPreferences("AppSettings", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val switch_Notification = findViewById<Switch>(R.id.switchNotifications)
        val switch_Sound = findViewById<Switch>(R.id.switchSound)
        val switch_Anonymous = findViewById<Switch>(R.id.switchAnonymous)
        val back = findViewById<ImageButton>(R.id.btnBack)
        val editProfile = findViewById<LinearLayout>(R.id.btnEditProfile)
        val readReceipts = findViewById<LinearLayout>(R.id.btnReadReceipts)
        val switch_ReadReceipts = findViewById<Switch>(R.id.switchReadReceipts)
        val notifications = findViewById<LinearLayout>(R.id.btnNotifications)
        val securityInfo = findViewById<LinearLayout>(R.id.btnSecurityInfo)
        val signOut = findViewById<TextView>(R.id.btnSignOut)

        switch_Notification.isChecked = preferences.getBoolean("notifications", true)

        switch_Notification.setOnCheckedChangeListener { _, isChecked ->

            preferences.edit()
                .putBoolean("notifications", isChecked)
                .apply()
        }


        back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        editProfile.setOnClickListener {
            val intent = Intent(this, Profile_Setup::class.java)
            startActivity(intent)
        }

        notifications.setOnClickListener {
            switch_Notification.toggle()
            when (switch_Notification.isChecked) {
                true -> switch_Notification.isChecked = false
                false -> switch_Notification.isChecked = true


            }
        }
        switch_Notification.setOnCheckedChangeListener { _, isChecked ->
            preferences.edit().putBoolean("notifications", isChecked).apply()
            switch_ReadReceipts.isChecked = preferences.getBoolean("read_receipts", true)

            switch_ReadReceipts.setOnCheckedChangeListener { _, isChecked ->
                preferences.edit().putBoolean("read_receipts", isChecked).apply()
            }
        }

        readReceipts.setOnClickListener {
            switch_ReadReceipts.toggle()

        }

        securityInfo.setOnClickListener{
            val user = auth.currentUser

            if(user != null){

                val email = user.email ?: "Unavailable"
                val emailStatus = if (user.isEmailVerified) "Verified" else "Not Verified"

                val signInMethod = user.providerData
                    .mapNotNull {it.providerId}
                    .filter {it != "firebase"}
                    .distinct()
                    .joinToString(", ")
                    .ifEmpty{"Unknown"}

                val securityDetails = """
                    Email: $email
                    Email Verification: $emailStatus
                    Sign-In Method: $signInMethod
                    Account Status: Active
                    """.trimIndent()

                AlertDialog.Builder(this)
                    .setTitle("Security Information")
                    .setMessage(securityDetails)
                    .setPositiveButton("Close", null)
                    .show()

            } else{
                AlertDialog.Builder(this)
                    .setTitle("Security Information")
                    .setMessage("User not signed in.")
                    .setPositiveButton("Close", null)
                    .show()
            }
        }


        signOut?.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}

