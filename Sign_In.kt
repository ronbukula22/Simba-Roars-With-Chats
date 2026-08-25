package com.example.simbachat

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.EditText
import android.widget.Toast
class Sign_In : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val SSO= findViewById<ImageView>(R.id.imageView)
        val Sign_In = findViewById<Button>(R.id.button3)
        val Phone_Number = findViewById<EditText>(R.id.editTextText3)

        Phone_Number.setOnClickListener{
            val phoneNumber = Phone_Number.text.toString()

            fun phoneNumberIsValid(phoneNumber: String): Boolean{
                return phoneNumber.length == 10
            }

            fun phoneNumberExists(phoneNumber: String): Boolean{
                return phoneNumber.isNotEmpty()
            }

            fun phoneNumberIsEmpty(phoneNumber: String): Boolean{
                return phoneNumber.isEmpty()
            }
            if(!phoneNumberIsValid(phoneNumber) || !phoneNumberExists(phoneNumber) && phoneNumberIsEmpty(phoneNumber)){
                    Toast.makeText(this, "Phone Number Is Invalid or Phone number doesn't exist or is empty", Toast.LENGTH_SHORT).show()
            }else if(phoneNumberIsValid(phoneNumber) || phoneNumberExists(phoneNumber)){
                Toast.makeText(this, "Check Passed",Toast.LENGTH_SHORT).show()
            }
            Sign_In.setOnClickListener{
                fun allFieldsAreFilled(): Boolean{
                    return phoneNumberIsValid(phoneNumber) && phoneNumberExists(phoneNumber) && phoneNumberIsEmpty(phoneNumber)
                }

                if(allFieldsAreFilled()){
                    Toast.makeText(this, "Sign In Successful!", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}