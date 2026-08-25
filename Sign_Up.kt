package com.example.simbachat

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.ImageButton
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class Sign_Up : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val SSO= findViewById<ImageButton>(R.id.SSO)
        val Sign_Up= findViewById<Button>(R.id.button2)
        val Email= findViewById<EditText>(R.id.editTextText)
        val Phone_Number= findViewById<EditText>(R.id.editTextText2)

        Sign_Up.setOnClickListener{
            val text = Email.text.toString()
            fun emailIsEmpty(text:String): Boolean{
                return text.isEmpty()
            }


            if(emailIsEmpty(text)){
                Toast.makeText(this, "ERROR:E-mail is empty", Toast.LENGTH_SHORT).show()
            }

            val number = Phone_Number.text.toString()
            fun phoneNumberIsValid(number: String): Boolean{
                return number.length == 10
            }
            if(!phoneNumberIsValid(number)){
                Toast.makeText(this, "ERROR:Phone number is invalid", Toast.LENGTH_SHORT).show()
            }

            if(phoneNumberIsValid(number) && !emailIsEmpty(text)){
                Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
            }


            fun SaveDetails(){
                val user_Email = Email.text.toString()
                val Cell_No = Phone_Number.text.toString()

                val auth = Firebase.auth
                val database = Firebase.firestore

                val user = hashMapOf(
                    "Email" to user_Email,
                    "Phone Number" to Cell_No,
                    "createdAt" to FieldValue.serverTimestamp()
                )

                database.collection("user_Info")
                    .document(auth.currentUser?.uid ?: return)
                    .set(user)
                    .addOnSuccessListener{
                        Log.d("Firestore", "Profile written")
                    }
                    .addOnFailureListener{

                        Log.w("Firestore", "Write failed")
                    }
            }
        }



    }
}