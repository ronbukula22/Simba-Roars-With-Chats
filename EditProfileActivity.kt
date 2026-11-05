
package com.example.simbachat.auth

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.simbachat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import android.util.Base64
import kotlin.math.max

class EditProfileActivity : AppCompatActivity() {

    private var selectedPhotoBase64: String? = null

    private lateinit var profilePhoto: ImageView

    private lateinit var nameInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var statusInput: EditText

    private lateinit var saveButton: TextView

    private val photoPicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                selectedPhotoBase64 = convertImageToBase64(uri)

                val imageBytes = Base64.decode(
                    selectedPhotoBase64,
                    Base64.DEFAULT
                )

                val bitmap = BitmapFactory.decodeByteArray(
                    imageBytes, 0, imageBytes.size
                )

                profilePhoto.setImageBitmap(bitmap)
                profilePhoto.visibility = ImageView.VISIBLE
                findViewById<TextView>(
                    R.id.editAvatarInitials
                ).visibility = TextView.GONE

            } catch (e: Exception) {
                selectedPhotoBase64 = null
                Toast.makeText(
                    this,
                    "Could not load selected picture: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        profilePhoto = findViewById(R.id.editProfilePhoto)
        nameInput = findViewById(R.id.editNameInput)
        phoneInput = findViewById(R.id.editPhoneInput)
        statusInput = findViewById(R.id.editStatusInput)

        saveButton = findViewById(R.id.saveEditProfileButton)

        findViewById<TextView>(R.id.editEmailText).text =
            FirebaseAuth.getInstance().currentUser?.email
                ?: "No email address"

        findViewById<TextView>(R.id.editProfileBack)
            .setOnClickListener { finish() }

        findViewById<TextView>(R.id.changePhotoButton)
            .setOnClickListener {
                photoPicker.launch("image/*")
            }

        saveButton.setOnClickListener {
            saveProfile()
        }

        loadProfile()
    }

    private fun convertImageToBase64(uri: Uri): String {
        val input = contentResolver.openInputStream(uri)
            ?: throw Exception("Cannot open selected image")

        val bitmap = input.use {
            BitmapFactory.decodeStream(it)
        } ?: throw Exception("Invalid image")

        val maxDimension = 512
        val scale = maxDimension.toFloat() /
                max(bitmap.width, bitmap.height)

        val resizedBitmap = if (scale < 1f) {
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scale).toInt().coerceAtLeast(1),
                (bitmap.height * scale).toInt().coerceAtLeast(1),
                true
            )
        } else {
            bitmap
        }

        val output = ByteArrayOutputStream()

        resizedBitmap.compress(
            Bitmap.CompressFormat.JPEG,
            65,
            output
        )

        return Base64.encodeToString(
            output.toByteArray(),
            Base64.NO_WRAP
        )
    }

    private fun loadProfile() {
        val user = FirebaseAuth.getInstance().currentUser
            ?: return

        lifecycleScope.launch {
            try {
                val document = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.uid)
                    .get()
                    .await()

                phoneInput.setText(
                    document.getString("phone_number") ?: ""
                )

                statusInput.setText(
                    document.getString("status") ?: ""
                )

                val savedPhoto = document.getString("photoBase64")

                if (!savedPhoto.isNullOrBlank()) {
                    val bytes = Base64.decode(
                        savedPhoto,
                        Base64.DEFAULT
                    )

                    val bitmap = BitmapFactory.decodeByteArray(
                        bytes, 0, bytes.size
                    )

                    if (bitmap != null) {
                        profilePhoto.setImageBitmap(bitmap)
                        profilePhoto.visibility = ImageView.VISIBLE
                        findViewById<TextView>(
                            R.id.editAvatarInitials
                        ).visibility = TextView.GONE
                    }
                } else {
                    showInitials(nameInput.text.toString())
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@EditProfileActivity,
                    "Failed to load profile: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showInitials(name: String) {
        val initials = name.trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") {
                it.first().uppercase()
            }

        findViewById<TextView>(R.id.editAvatarInitials).apply {
            text = initials.ifBlank { "SC" }
            visibility = TextView.VISIBLE
        }

        profilePhoto.visibility = ImageView.GONE
    }

    private fun saveProfile() {
        val user = FirebaseAuth.getInstance().currentUser
            ?: return

        val name = nameInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()
        val status = statusInput.text.toString().trim()

        if (name.isBlank()) {
            nameInput.error = "Please enter your name"
            nameInput.requestFocus()
            return
        }

        saveButton.isEnabled = false
        saveButton.text = "Saving..."

        lifecycleScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()

                val document = db.collection("users")
                    .document(user.uid)

                val oldData = document.get().await()

                val profileData = hashMapOf(
                    "uid" to user.uid,
                    "name" to name,
                    "email" to user.email,
                    "phone_number" to phone,
                    "status" to status,
                    "photo_Url" to "",
                    "photoBase64" to (
                            selectedPhotoBase64
                                ?: oldData.getString("photoBase64")
                                ?: ""
                            )
                )

                document.set(
                    profileData,
                    SetOptions.merge()
                ).await()

                Toast.makeText(
                    this@EditProfileActivity,
                    "Profile updated successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

            } catch (e: Exception) {
                saveButton.isEnabled = true
                saveButton.text = "Save Changes"

                Toast.makeText(
                    this@EditProfileActivity,
                    "Error saving profile: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
