package com.example.simbachat.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.simbachat.R
import com.example.simbachat.dashboard.DashboardActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {

    private lateinit var phoneNumber: String
    private lateinit var auth: FirebaseAuth

    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_otp)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->

            val bars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                bars.left,
                bars.top,
                bars.right,
                bars.bottom
            )

            insets
        }

        phoneNumber =
            intent.getStringExtra("phone") ?: ""

        auth =
            FirebaseAuth.getInstance()

        val generateButton =
            findViewById<Button>(R.id.OTP_GEN)

        val verifyButton =
            findViewById<Button>(R.id.button3)

        val otpInput =
            findViewById<EditText>(R.id.editTextText3)


       
        // SEND OTP


        generateButton.setOnClickListener {

            if (phoneNumber.isEmpty()) {

                Toast.makeText(
                    this,
                    "Phone number is missing",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            val callbacks =
                object :
                    PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    override fun onVerificationCompleted(
                        credential: PhoneAuthCredential
                    ) {

                        // Firebase automatically verified the number.
                        signInWithPhoneAuthCredential(credential)
                    }

                    override fun onVerificationFailed(
                        e: FirebaseException
                    ) {

                        Toast.makeText(
                            this@OtpActivity,
                            "Verification failed: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {

                        this@OtpActivity.verificationId =
                            verificationId

                        this@OtpActivity.resendToken =
                            token

                        Toast.makeText(
                            this@OtpActivity,
                            "OTP sent to $phoneNumber",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            val options =
                PhoneAuthOptions
                    .newBuilder(auth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(
                        60L,
                        TimeUnit.SECONDS
                    )
                    .setActivity(this)
                    .setCallbacks(callbacks)
                    .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }



        // VERIFY OTP


        verifyButton.setOnClickListener {

            val enteredCode =
                otpInput.text.toString().trim()

            when {

                OTPValidator.isOtpEmpty(enteredCode) -> {

                    otpInput.error =
                        "Enter the one-time PIN"
                }

                enteredCode.length != 6 -> {

                    otpInput.error =
                        "OTP must contain 6 digits"
                }

                verificationId == null -> {

                    Toast.makeText(
                        this,
                        "Send the OTP first",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {

                    val credential =
                        PhoneAuthProvider.getCredential(
                            verificationId!!,
                            enteredCode
                        )

                    signInWithPhoneAuthCredential(
                        credential
                    )
                }
            }
        }
    }



    // FIREBASE LOGIN


    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential
    ) {

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (!task.isSuccessful) {

                    Toast.makeText(
                        this,
                        "Incorrect OTP",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnCompleteListener
                }


                // Firebase login successful
                val user =
                    auth.currentUser

                if (user == null) {

                    Toast.makeText(
                        this,
                        "Authentication failed",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnCompleteListener
                }


                Toast.makeText(
                    this,
                    "Account Verified",
                    Toast.LENGTH_SHORT
                ).show()



                // CHECK FIRESTORE PROFILE


                val userRepo =
                    User_Repo()

                lifecycleScope.launch {

                    try {

                        val existingProfile =
                            userRepo.get_Profile(user.uid)


                        if (existingProfile == null) {


                            // NEW USER
                            // Go to Profile Setup


                            val setupIntent =
                                Intent(
                                    this@OtpActivity,
                                    ProfileSetupActivity::class.java
                                )

                            startActivity(setupIntent)

                        } else {


                            // EXISTING USER
                            // Go to Dashboard


                            val dashboardIntent =
                                Intent(
                                    this@OtpActivity,
                                    DashboardActivity::class.java
                                )

                            startActivity(dashboardIntent)
                        }

                        finish()

                    } catch (e: Exception) {

                        Toast.makeText(
                            this@OtpActivity,
                            "Could not load your profile: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }
}