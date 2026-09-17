package com.example.simbachat.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simbachat.R

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        val emailInput = findViewById<EditText>(R.id.editTextText)
        val phoneInput = findViewById<EditText>(R.id.editTextText2)
        val signUpButton = findViewById<Button>(R.id.button2)

        signUpButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()

            when {
                email.isEmpty() -> emailInput.error = "Email is required"
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                    emailInput.error = "Enter a valid email address"
                !phone.matches(Regex("\\d{10}")) ->
                    phoneInput.error = "Enter a valid 10-digit phone number"
                else ->
                    Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
