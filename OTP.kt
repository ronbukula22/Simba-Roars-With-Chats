package com.example.simbachat

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random
import com.example.simbachat.R
import android.widget.Toast
import android.widget.Button

class OTP : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_otp)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val OTP_GEN = findViewById<Button>(R.id.OTP_GEN)

        val Sign_Up = findViewById<Button>(R.id.button3)

        val OTP_CODE = findViewById<EditText>(R.id.editTextText3)
        OTP_GEN.setOnClickListener{
           val Otp =  Random.nextInt(1000000,9999999)

            Toast.makeText(this,"Your OTP will be sent to you. Check your messages",Toast.LENGTH_SHORT).show()

           // push notifications for OTP

        }

        Sign_Up.setOnClickListener{
            val enteredCode = OTP_CODE.text.toString()

            if(!OTPValidator.isOtpLengthCorrect(enteredCode)){
                Toast.makeText(this, "INCORRECT OTP",Toast.LENGTH_SHORT).show()
            }

            if(OTPValidator.isOtpEmpty(enteredCode)){
                Toast.makeText(this, "ENTER THE ONE TIME PIN!",Toast.LENGTH_SHORT).show()
            }else if(OTPValidator.isOtpLengthCorrect(enteredCode) && !OTPValidator.isOtpEmpty(enteredCode)){
                Toast.makeText(this, "Account Varified",Toast.LENGTH_SHORT).show()

                val intent = Intent(this, Dashboard::class.java)
                startActivity(intent)
            }
        }
    }
}