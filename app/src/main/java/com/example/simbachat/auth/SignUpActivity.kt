package com.example.simbachat.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simbachat.R
import com.example.simbachat.dashboard.DashboardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText

    private lateinit var createAccountButton: Button
    private lateinit var goToSignInText: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->

            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                bars.left,
                bars.top,
                bars.right,
                bars.bottom
            )

            insets
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        nameInput = findViewById(R.id.registerNameInput)
        emailInput = findViewById(R.id.registerEmailInput)
        phoneInput = findViewById(R.id.registerPhoneInput)
        passwordInput = findViewById(R.id.registerPasswordInput)
        confirmPasswordInput =
            findViewById(R.id.registerConfirmPasswordInput)

        createAccountButton =
            findViewById(R.id.createAccountButton)

        goToSignInText =
            findViewById(R.id.goToSignInText)

        progressBar =
            findViewById(R.id.registerProgressBar)

        progressBar.visibility = View.GONE

        createAccountButton.setOnClickListener {
            registerUser()
        }

        goToSignInText.setOnClickListener {
            finish()
        }
    }

    private fun registerUser() {

        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()
        val password = passwordInput.text.toString()
        val confirmPassword =
            confirmPasswordInput.text.toString()

        if (name.isEmpty()) {
            nameInput.error = "Full name is required"
            nameInput.requestFocus()
            return
        }

        if (email.isEmpty()) {
            emailInput.error = "Email is required"
            emailInput.requestFocus()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email).matches()
        ) {
            emailInput.error = "Enter a valid email address"
            emailInput.requestFocus()
            return
        }

        if (phone.isNotEmpty()) {
            val digits = phone.filter { it.isDigit() }

            if (digits.length !in 7..15) {
                phoneInput.error = "Enter a valid phone number"
                phoneInput.requestFocus()
                return
            }
        }

        if (password.length < 6) {
            passwordInput.error =
                "Password must contain at least 6 characters"
            passwordInput.requestFocus()
            return
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordInput.error =
                "Please confirm your password"
            confirmPasswordInput.requestFocus()
            return
        }

        if (password != confirmPassword) {
            confirmPasswordInput.error =
                "Passwords do not match"
            confirmPasswordInput.requestFocus()
            return
        }

        setLoading(true)

        // Create the Firebase Authentication account.
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    val user = auth.currentUser

                    if (user == null) {
                        setLoading(false)
                        Toast.makeText(
                            this,
                            "Account created, but user information could not be loaded.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@addOnCompleteListener
                    }

                    // Prepare the user's Firestore profile.
                    val profile = hashMapOf(
                        "uid" to user.uid,
                        "name" to name,
                        "email" to email,
                        "phone_number" to phone,
                        "status" to "Hi there! I'm roaring with chats",
                        "photo_Url" to "",
                        "createdAt" to FieldValue.serverTimestamp(),
                        "lastSeen" to FieldValue.serverTimestamp()
                    )

                    // Save the profile under users/{uid}.
                    db.collection("users")
                        .document(user.uid)
                        .set(profile)
                        .addOnSuccessListener {

                            setLoading(false)

                            Toast.makeText(
                                this,
                                "Account created successfully!",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(
                                this,
                                DashboardActivity::class.java
                            )

                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK

                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { error ->

                            setLoading(false)

                            auth.signOut()

                            Toast.makeText(
                                this,
                                "Your account was created, but your profile could not be saved: ${error.localizedMessage}",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                } else {

                    setLoading(false)

                    Toast.makeText(
                        this,
                        "Registration failed: ${
                            task.exception?.localizedMessage
                                ?: "Please try again."
                        }",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun setLoading(isLoading: Boolean) {

        progressBar.visibility =
            if (isLoading) View.VISIBLE else View.GONE

        createAccountButton.isEnabled = !isLoading
        goToSignInText.isEnabled = !isLoading
    }
}