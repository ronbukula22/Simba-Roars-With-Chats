package com.example.simbachat

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.simbachat.databinding.ActivityProfileSetupBinding

class Profile_Setup : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}