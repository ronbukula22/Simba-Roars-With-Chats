package com.example.simbachat.auth

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.simbachat.R
import com.example.simbachat.dashboard.DashboardActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userRepo: User_Repo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile_setup)

        auth = FirebaseAuth.getInstance()
        userRepo = User_Repo()

        val nameInput =
            findViewById<EditText>(R.id.profileNameInput)

        val statusInput =
            findViewById<EditText>(R.id.profileStatusInput)

        val phoneText =
            findViewById<TextView>(R.id.profilePhoneText)

        val avatar =
            findViewById<TextView>(R.id.setupAvatar)

        val saveButton =
            findViewById<TextView>(R.id.saveProfileButton)

        val currentUser =
            auth.currentUser

        if (currentUser == null) {

            Toast.makeText(
                this,
                "You are not signed in",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        val phoneNumber =
            currentUser.phoneNumber ?: ""

        phoneText.text =
            "Phone: $phoneNumber"

        saveButton.setOnClickListener {

            val name =
                nameInput.text.toString().trim()

            val status =
                statusInput.text.toString().trim()

            if (name.isEmpty()) {

                nameInput.error =
                    "Enter your name"

                nameInput.requestFocus()

                return@setOnClickListener
            }

            val finalStatus =
                if (status.isEmpty()) {
                    "Hi there! I'm roaring with chats"
                } else {
                    status
                }

            saveButton.isEnabled = false
            saveButton.text = "Saving..."

            lifecycleScope.launch {

                try {

                    userRepo.saveProfile(
                        name = name,
                        phone_number = phoneNumber,
                        status = finalStatus,
                        photo_Url = ""
                    )

                    avatar.text =
                        name
                            .trim()
                            .split(" ")
                            .filter { it.isNotBlank() }
                            .take(2)
                            .joinToString("") {
                                it.first().uppercase()
                            }

                    Toast.makeText(
                        this@ProfileSetupActivity,
                        "Profile saved successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent =
                        Intent(
                            this@ProfileSetupActivity,
                            DashboardActivity::class.java
                        )

                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)

                } catch (e: Exception) {

                    saveButton.isEnabled = true
                    saveButton.text = "Save Profile"

                    Toast.makeText(
                        this@ProfileSetupActivity,
                        "Could not save profile: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}