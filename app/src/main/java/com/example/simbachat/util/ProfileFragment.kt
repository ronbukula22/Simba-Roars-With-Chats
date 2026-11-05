
package com.example.simbachat.util

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.simbachat.R
import com.example.simbachat.auth.EditProfileActivity
import com.example.simbachat.auth.SignInActivity
import com.example.simbachat.auth.User_Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration

class ProfileFragment : Fragment() {

    private var profileListener: ListenerRegistration? = null
    private val prefsName = "simba_profile_settings"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser

        val profileName = view.findViewById<TextView>(R.id.profileName)
        val profileAvatar = view.findViewById<TextView>(R.id.profileAvatar)
        val profileAvatarImage =
            view.findViewById<ImageView>(R.id.profileAvatarImage)
        val profileContact = view.findViewById<TextView>(R.id.profileContact)

        profileContact.text =
            currentUser?.phoneNumber ?: "No phone number"

        view.findViewById<View>(R.id.editProfileButton)
            .setOnClickListener {
                startActivity(
                    Intent(requireContext(), EditProfileActivity::class.java)
                )
            }

        profileListener = User_Repo().observeProfile { user ->
            if (!isAdded) return@observeProfile

            profileName.text = user.name.ifBlank { "Your Name" }

            val initials = user.name.trim()
                .split(Regex("\\s+"))
                .filter { it.isNotBlank() }
                .take(2)
                .joinToString("") { it.first().uppercase() }

            profileAvatar.text = initials.ifBlank { "SC" }

            profileContact.text = user.phone_number
                .ifBlank { currentUser?.phoneNumber ?: "No phone number" }

            val encodedPhoto = user.photoBase64

            if (encodedPhoto.isNotBlank()) {
                try {
                    val imageBytes = Base64.decode(
                        encodedPhoto,
                        Base64.DEFAULT
                    )

                    val bitmap = BitmapFactory.decodeByteArray(
                        imageBytes,
                        0,
                        imageBytes.size
                    )

                    if (bitmap != null) {
                        profileAvatarImage.setImageBitmap(bitmap)
                        profileAvatarImage.visibility = View.VISIBLE
                        profileAvatar.visibility = View.GONE
                    } else {
                        profileAvatarImage.visibility = View.GONE
                        profileAvatar.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    profileAvatarImage.visibility = View.GONE
                    profileAvatar.visibility = View.VISIBLE
                }
            } else {
                profileAvatarImage.visibility = View.GONE
                profileAvatar.visibility = View.VISIBLE
            }
        }

        val prefs = requireContext().getSharedPreferences(
            prefsName,
            android.content.Context.MODE_PRIVATE
        )

        val saNewsSwitch = view.findViewById<Switch>(R.id.saNewsSwitch)
        saNewsSwitch.isChecked = prefs.getBoolean("sa_news", true)
        saNewsSwitch.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("sa_news", checked).apply()
        }

        val globalNewsSwitch = view.findViewById<Switch>(R.id.globalNewsSwitch)
        globalNewsSwitch.isChecked = prefs.getBoolean("global_news", false)
        globalNewsSwitch.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("global_news", checked).apply()
        }

        val anonymousSwitch = view.findViewById<Switch>(R.id.anonymousSwitch)
        val anonymousInfo = view.findViewById<TextView>(R.id.anonymousInfo)

        anonymousSwitch.isChecked =
            prefs.getBoolean("anonymous_mode", false)

        anonymousInfo.visibility =
            if (anonymousSwitch.isChecked) View.VISIBLE else View.GONE

        anonymousSwitch.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("anonymous_mode", checked).apply()
            anonymousInfo.visibility =
                if (checked) View.VISIBLE else View.GONE
        }

        view.findViewById<View>(R.id.privacySetting)
            .setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        PrivacySecurityActivity::class.java
                    )
                )
            }

        view.findViewById<View>(R.id.appearanceSetting)
            .setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        AppearanceActivity::class.java
                    )
                )
            }

        view.findViewById<View>(R.id.helpSetting)
            .setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        HelpSupportActivity::class.java
                    )
                )
            }

        view.findViewById<View>(R.id.signOutButton)
            .setOnClickListener {
                FirebaseAuth.getInstance().signOut()

                Toast.makeText(
                    requireContext(),
                    "Signed out successfully",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(
                    requireContext(),
                    SignInActivity::class.java
                )

                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
            }
    }

    override fun onDestroyView() {
        profileListener?.remove()
        profileListener = null
        super.onDestroyView()
    }
}
