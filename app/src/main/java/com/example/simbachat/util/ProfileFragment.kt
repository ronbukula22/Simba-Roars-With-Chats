package com.example.simbachat.util

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.simbachat.R
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

        val view = inflater.inflate(
            R.layout.fragment_profile,
            container,
            false
        )


        // FIREBASE USER


        val currentUser =
            FirebaseAuth.getInstance().currentUser

        val profileName =
            view.findViewById<TextView>(R.id.profileName)
        val profileAvatar =
            view.findViewById<TextView>(R.id.profileAvatar)

        val profileContact =
            view.findViewById<TextView>(R.id.profileContact)

        if (currentUser != null) {

            profileContact.text =
                currentUser.phoneNumber ?: "No phone number"
        }



        // LOAD PROFILE FROM FIRESTORE


        val userRepo = User_Repo()

        profileListener =
            userRepo.observeProfile { user ->

                if (!isAdded) return@observeProfile

                profileName.text =
                    if (user.name.isNotBlank()) {
                        user.name
                    } else {
                        "Your Name"
                    }

                profileAvatar.text =
                    if (user.name.isNotBlank()) {

                        user.name
                            .trim()
                            .split(" ")
                            .filter { it.isNotBlank() }
                            .take(2)
                            .joinToString("") {
                                it.first().uppercase()
                            }

                    } else {
                        "TN"
                    }

                profileContact.text =
                    if (user.phone_number.isNotBlank()) {
                        user.phone_number
                    } else {
                        currentUser?.phoneNumber
                            ?: "No phone number"
                    }
            }



        // SETTINGS STORAGE


        val preferences =
            requireContext().getSharedPreferences(
                prefsName,
                0
            )



        // NEWS SWITCHES


        val saNewsSwitch =
            view.findViewById<Switch>(R.id.saNewsSwitch)

        val globalNewsSwitch =
            view.findViewById<Switch>(R.id.globalNewsSwitch)

        val breakingSwitch =
            view.findViewById<Switch>(R.id.breakingSwitch)


        // Load saved values

        saNewsSwitch.isChecked =
            preferences.getBoolean(
                "sa_news",
                true
            )

        globalNewsSwitch.isChecked =
            preferences.getBoolean(
                "global_news",
                false
            )

        breakingSwitch.isChecked =
            preferences.getBoolean(
                "breaking_news",
                true
            )


        // Save SA News

        saNewsSwitch.setOnCheckedChangeListener { _, isChecked ->

            preferences.edit()
                .putBoolean(
                    "sa_news",
                    isChecked
                )
                .apply()

            val message =
                if (isChecked) {
                    "South Africa news notifications enabled"
                } else {
                    "South Africa news notifications disabled"
                }

            Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_SHORT
            ).show()
        }


        // Save Global News

        globalNewsSwitch.setOnCheckedChangeListener { _, isChecked ->

            preferences.edit()
                .putBoolean(
                    "global_news",
                    isChecked
                )
                .apply()

            val message =
                if (isChecked) {
                    "Global news notifications enabled"
                } else {
                    "Global news notifications disabled"
                }

            Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_SHORT
            ).show()
        }


        // Save Breaking Alerts

        breakingSwitch.setOnCheckedChangeListener { _, isChecked ->

            preferences.edit()
                .putBoolean(
                    "breaking_news",
                    isChecked
                )
                .apply()

            val message =
                if (isChecked) {
                    "Breaking alerts enabled"
                } else {
                    "Breaking alerts disabled"
                }

            Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_SHORT
            ).show()
        }



        // ANONYMOUS MODE


        val anonymousSwitch =
            view.findViewById<Switch>(
                R.id.anonymousSwitch
            )

        val anonymousInfo =
            view.findViewById<TextView>(
                R.id.anonymousInfo
            )


        // Load saved Anonymous Mode

        anonymousSwitch.isChecked =
            preferences.getBoolean(
                "anonymous_mode",
                false
            )

        anonymousInfo.visibility =
            if (anonymousSwitch.isChecked) {
                View.VISIBLE
            } else {
                View.GONE
            }


        // Save Anonymous Mode

        anonymousSwitch.setOnCheckedChangeListener { _, isChecked ->

            preferences.edit()
                .putBoolean(
                    "anonymous_mode",
                    isChecked
                )
                .apply()

            anonymousInfo.visibility =
                if (isChecked) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            val message =
                if (isChecked) {
                    "Anonymous Mode enabled"
                } else {
                    "Anonymous Mode disabled"
                }

            Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_SHORT
            ).show()
        }



        // SETTINGS


        val privacySetting =
            view.findViewById<TextView>(
                R.id.privacySetting
            )

        val moderationSetting =
            view.findViewById<TextView>(
                R.id.moderationSetting
            )

        val appearanceSetting =
            view.findViewById<TextView>(
                R.id.appearanceSetting
            )

        val devicesSetting =
            view.findViewById<TextView>(
                R.id.devicesSetting
            )

        val helpSetting =
            view.findViewById<TextView>(
                R.id.helpSetting
            )


        privacySetting.setOnClickListener {

            Toast.makeText(
                requireContext(),
                "Privacy & Security",
                Toast.LENGTH_SHORT
            ).show()
        }

        moderationSetting.setOnClickListener {

            Toast.makeText(
                requireContext(),
                "Room Moderation History",
                Toast.LENGTH_SHORT
            ).show()
        }

        appearanceSetting.setOnClickListener {

            Toast.makeText(
                requireContext(),
                "Appearance",
                Toast.LENGTH_SHORT
            ).show()
        }

        devicesSetting.setOnClickListener {

            Toast.makeText(
                requireContext(),
                "Linked Devices",
                Toast.LENGTH_SHORT
            ).show()
        }

        helpSetting.setOnClickListener {

            Toast.makeText(
                requireContext(),
                "Help & Support",
                Toast.LENGTH_SHORT
            ).show()
        }



        // SIGN OUT


        val signOutButton =
            view.findViewById<TextView>(
                R.id.signOutButton
            )

        signOutButton.setOnClickListener {

            FirebaseAuth
                .getInstance()
                .signOut()

            Toast.makeText(
                requireContext(),
                "Signed out successfully",
                Toast.LENGTH_SHORT
            ).show()

            val intent =
                Intent(
                    requireContext(),
                    SignInActivity::class.java
                )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }


        return view
    }


    override fun onDestroyView() {

        profileListener?.remove()
        profileListener = null

        super.onDestroyView()
    }
}