
package com.example.simbachat.util

import android.os.Bundle
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.simbachat.R

class AppearanceActivity : AppCompatActivity() {

    private lateinit var appearanceGroup: RadioGroup
    private lateinit var saveStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appearance)

        appearanceGroup = findViewById(R.id.appearanceGroup)
        saveStatus = findViewById(R.id.appearanceSaveStatus)

        val preferences = getSharedPreferences(
            "appearance_settings",
            MODE_PRIVATE
        )

        val savedMode = preferences.getString(
            "appearance_mode",
            "system"
        )

        appearanceGroup.check(
            if (savedMode == "light") {
                R.id.appearanceLight
            } else {
                R.id.appearanceSystem
            }
        )

        findViewById<TextView>(R.id.appearanceBack)
            .setOnClickListener {
                finish()
            }

        appearanceGroup.setOnCheckedChangeListener { _, checkedId ->

            val mode = if (checkedId == R.id.appearanceLight) {
                "light"
            } else {
                "system"
            }

            preferences.edit()
                .putString("appearance_mode", mode)
                .apply()

            AppCompatDelegate.setDefaultNightMode(
                if (mode == "light") {
                    AppCompatDelegate.MODE_NIGHT_NO
                } else {
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            )

            saveStatus.text = "Appearance saved"
        }
    }
}
