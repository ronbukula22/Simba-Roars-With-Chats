package com.example.simbachat.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.simbachat.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.hbb20.CountryCodePicker
import kotlinx.coroutines.launch


class SignInActivity : AppCompatActivity() {

    private lateinit var countryCodePicker: CountryCodePicker
    private lateinit var phoneNumberInput: EditText
    private lateinit var sendOtpButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                bars.left,
                bars.top,
                bars.right,
                bars.bottom
            )

            insets
        }

        countryCodePicker = findViewById(R.id.login_countrycode)
        phoneNumberInput = findViewById(R.id.login_mobile_number)
        sendOtpButton = findViewById(R.id.send_otp_btn)
        progressBar = findViewById(R.id.login_progress_bar)
        val SSO = findViewById<ImageButton>(R.id.SSO)

        progressBar.visibility = View.GONE

        countryCodePicker.registerCarrierNumberEditText(phoneNumberInput)

        sendOtpButton.setOnClickListener {

            if (!countryCodePicker.isValidFullNumber) {
                phoneNumberInput.error = "Phone number not valid"
                return@setOnClickListener
            }

            val phoneNumber = countryCodePicker.fullNumberWithPlus

            sendOtpButton.isEnabled = false
            progressBar.visibility = View.VISIBLE

            val intent = Intent(
                this,
                OtpActivity::class.java
            )

            intent.putExtra("phone", phoneNumber)

            startActivity(intent)

            sendOtpButton.isEnabled = true
            progressBar.visibility = View.GONE
        }

        SSO.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(this)

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(this@SignInActivity, request)
                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                firebaseAuthWithGoogle(idToken)
            } catch (e: GetCredentialException) {
                Toast.makeText(this@SignInActivity, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
                Log.e("Google Sign-In Error", e.message.toString(), e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                Toast.makeText(this, "Google Sign-In Successful", Toast.LENGTH_SHORT).show()
                println("User: ${user?.email}")
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
                println("Error: ${exception.message}")
            }
    }
}