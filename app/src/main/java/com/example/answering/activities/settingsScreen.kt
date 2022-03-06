package com.example.answering.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.answering.R
import com.example.answering.databinding.ActivityMainBinding.inflate
import com.example.answering.databinding.ActivitySettingsScreenBinding
import com.example.answering.databinding.ActivitySettingsScreenBinding.inflate
import com.example.answering.databinding.BeginnerBinding.inflate

private lateinit var binding : ActivitySettingsScreenBinding
class SettingsScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySettingsScreenBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()






    }
}