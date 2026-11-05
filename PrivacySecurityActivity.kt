package com.example.simbachat.util

import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.simbachat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.SetOptions

class PrivacySecurityActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private var isLoading = true

    private lateinit var profileVisibilityGroup: RadioGroup
    private lateinit var lastSeenGroup: RadioGroup
    private lateinit var readReceiptsSwitch: Switch
    private lateinit var saveStatus: TextView

    private val userId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_security)

        profileVisibilityGroup =
            findViewById(R.id.profileVisibilityGroup)

        lastSeenGroup =
            findViewById(R.id.lastSeenGroup)

        readReceiptsSwitch =
            findViewById(R.id.readReceiptsSwitch)

        saveStatus =
            findViewById(R.id.privacySaveStatus)

        findViewById<TextView>(R.id.privacyBack)
            .setOnClickListener {
                finish()
            }



        loadPrivacySettings()
    }

    private fun loadPrivacySettings() {
        val uid = userId

        if (uid == null) {
            Toast.makeText(this, "Please sign in again.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                val profileVisibility =
                    document.getString("profileVisibility") ?: "everyone"

                val lastSeenVisibility =
                    document.getString("lastSeenVisibility") ?: "everyone"

                val readReceipts =
                    document.getBoolean("readReceipts") ?: true

                profileVisibilityGroup.check(
                    if (profileVisibility == "nobody") {
                        R.id.visibilityNobody
                    } else {
                        R.id.visibilityEveryone
                    }
                )

                lastSeenGroup.check(
                    if (lastSeenVisibility == "nobody") {
                        R.id.lastSeenNobody
                    } else {
                        R.id.lastSeenEveryone
                    }
                )

                readReceiptsSwitch.isChecked = readReceipts

                isLoading = false
                setupListeners()
            }
            .addOnFailureListener { error ->
                isLoading = false
                setupListeners()

                saveStatus.text = "Could not load settings."
                Toast.makeText(
                    this,
                    error.localizedMessage ?: "Unable to load settings",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun setupListeners() {

        profileVisibilityGroup.setOnCheckedChangeListener { _, checkedId ->
            if (isLoading) return@setOnCheckedChangeListener

            val value = if (checkedId == R.id.visibilityNobody) {
                "nobody"
            } else {
                "everyone"
            }

            saveSetting("profileVisibility", value)
        }

        lastSeenGroup.setOnCheckedChangeListener { _, checkedId ->
            if (isLoading) return@setOnCheckedChangeListener

            val value = if (checkedId == R.id.lastSeenNobody) {
                "nobody"
            } else {
                "everyone"
            }

            saveSetting("lastSeenVisibility", value)
        }

        readReceiptsSwitch.setOnCheckedChangeListener { _, checked ->
            if (isLoading) return@setOnCheckedChangeListener

            saveSetting("readReceipts", checked)
        }
    }

    private fun saveSetting(field: String, value: Any) {
        val uid = userId ?: return

        saveStatus.text = "Saving..."

        db.collection("users")
            .document(uid)
            .set(
                mapOf(field to value),
                SetOptions.merge()
            )
            .addOnSuccessListener {
                saveStatus.text = "Settings saved"
            }
            .addOnFailureListener { error ->
                saveStatus.text = "Could not save setting"

                Toast.makeText(
                    this,
                    error.localizedMessage ?: "Save failed",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}