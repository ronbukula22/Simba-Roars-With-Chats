
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
import androidx.appcompat.app.AppCompatDelegate

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var signInButton: Button
    private lateinit var forgotPasswordText: TextView
    private lateinit var goToSignUpText: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {

        val appearancePrefs = getSharedPreferences(
            "appearance_settings",
            MODE_PRIVATE
        )

        val appearanceMode = appearancePrefs.getString(
            "appearance_mode",
            "system"
        )

        AppCompatDelegate.setDefaultNightMode(
            if (appearanceMode == "light") {
                AppCompatDelegate.MODE_NIGHT_NO
            } else {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)

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

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Check whether the user is already signed in.
        if (auth.currentUser != null) {

            val intent = Intent(
                this,
                DashboardActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
            return
        }

        // Connect the XML components
        emailInput = findViewById(R.id.loginEmailInput)
        passwordInput = findViewById(R.id.loginPasswordInput)
        signInButton = findViewById(R.id.signInButton)
        forgotPasswordText = findViewById(R.id.forgotPasswordText)
        goToSignUpText = findViewById(R.id.goToSignUpText)
        progressBar = findViewById(R.id.loginProgressBar)

        progressBar.visibility = View.GONE

        // Sign in button
        signInButton.setOnClickListener {
            signInUser()
        }

        // Forgot password
        forgotPasswordText.setOnClickListener {
            resetPassword()
        }

        // Create account
        goToSignUpText.setOnClickListener {
            startActivity(
                Intent(this, SignUpActivity::class.java)
            )
        }
    }

    private fun signInUser() {

        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        // Validate email
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

        // Validate password
        if (password.isEmpty()) {
            passwordInput.error = "Password is required"
            passwordInput.requestFocus()
            return
        }

        // Show loading
        setLoading(true)

        // Authenticate using Firebase
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                setLoading(false)

                if (task.isSuccessful) {

                    Toast.makeText(
                        this,
                        "Welcome back to SimbaChat!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Open dashboard
                    val intent = Intent(
                        this,
                        DashboardActivity::class.java
                    )

                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)
                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "Sign in failed: ${
                            task.exception?.localizedMessage
                                ?: "Please check your details."
                        }",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun resetPassword() {

        val email = emailInput.text.toString().trim()

        if (email.isEmpty()) {
            emailInput.error =
                "Enter your email address first"
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

        setLoading(true)

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(this) { task ->

                setLoading(false)

                if (task.isSuccessful) {

                    Toast.makeText(
                        this,
                        "Password reset email sent. Check your inbox.",
                        Toast.LENGTH_LONG
                    ).show()

                } else {

                    Toast.makeText(
                        this,
                        task.exception?.localizedMessage
                            ?: "Unable to send password reset email.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun setLoading(isLoading: Boolean) {

        progressBar.visibility =
            if (isLoading) View.VISIBLE else View.GONE

        signInButton.isEnabled = !isLoading
        forgotPasswordText.isEnabled = !isLoading
        goToSignUpText.isEnabled = !isLoading
    }
}
