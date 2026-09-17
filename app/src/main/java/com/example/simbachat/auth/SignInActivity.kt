package com.example.simbachat.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.simbachat.R
import com.hbb20.CountryCodePicker

import android.widget.Toast

class SignInActivity : AppCompatActivity() {

    private lateinit var countryCodePicker: CountryCodePicker
    private lateinit var phoneNumberInput: EditText
    private lateinit var sendOtpButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)

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

        progressBar.visibility = View.GONE

        countryCodePicker.registerCarrierNumberEditText(phoneNumberInput)

        sendOtpButton.setOnClickListener {

            if (!countryCodePicker.isValidFullNumber) {

                phoneNumberInput.error = "Phone number not valid"

                return@setOnClickListener
            }

            val phoneNumber = countryCodePicker.fullNumberWithPlus

            val intent = Intent(
                this,
                OtpActivity::class.java
            )

            intent.putExtra("phone", phoneNumber)

            startActivity(intent)
        }
    }
}