package com.example.simbachat.util

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.simbachat.R

class HelpSupportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_support)

        findViewById<TextView>(R.id.helpBack).setOnClickListener {
            finish()
        }

        // FAQ questions and answers
        setupFaq(
            R.id.faqAccount,
            "How do I edit my profile?",
            "Open your Profile and tap Edit Profile. You can update your name, phone number, status and profile picture."
        )

        setupFaq(
            R.id.faqPrivacy,
            "How do I change my privacy settings?",
            "Open Profile, select Privacy & Security, and choose your preferred profile visibility, last seen and read receipt settings."
        )

        setupFaq(
            R.id.faqPhoto,
            "How do I change my profile picture?",
            "Open Edit Profile, select your picture, choose an image from your device and tap Save Changes."
        )

        setupFaq(
            R.id.faqSignOut,
            "How do I sign out?",
            "Open your Profile and tap the Sign Out button."
        )

        // Contact support
        findViewById<TextView>(R.id.contactSupport)
            .setOnClickListener {

                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(
                        Intent.EXTRA_SUBJECT,
                        "SimbaChat Support Request"
                    )
                }

                try {
                    startActivity(
                        Intent.createChooser(
                            emailIntent,
                            "Contact SimbaChat Support"
                        )
                    )
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "No email application found.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        // About SimbaChat
        findViewById<TextView>(R.id.aboutSimbaChat)
            .setOnClickListener {

                AlertDialog.Builder(this)
                    .setTitle("About SimbaChat")
                    .setMessage(
                        "SimbaChat\n\n" +
                                "Connect, communicate and stay in touch.\n\n" +
                                "Thank you for using SimbaChat!"
                    )
                    .setPositiveButton("Close", null)
                    .show()
            }

        // Display app version
        val versionName = try {
            packageManager.getPackageInfo(packageName, 0).versionName
                ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }

        findViewById<TextView>(R.id.helpAppVersion).text =
            "SimbaChat Version $versionName"
    }

    private fun setupFaq(
        viewId: Int,
        question: String,
        answer: String
    ) {
        val faq = findViewById<TextView>(viewId)

        faq.setOnClickListener {
            val expanded = faq.tag == "expanded"

            if (expanded) {
                faq.text = "$question  +"
                faq.tag = "collapsed"
            } else {
                faq.text = "$question\n\n$answer\n\nTap to collapse  −"
                faq.tag = "expanded"
            }
        }
    }
}