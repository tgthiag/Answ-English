package com.example.answering

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.answering.activities.SettingsScreen
import com.example.answering.activities.beginner


import com.example.answering.databinding.ActivityMainBinding


private lateinit var binding : ActivityMainBinding
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()



        binding.btAboutYou.setOnClickListener {
            val intent = Intent(this, beginner::class.java)
            intent.putExtra("level", "basic")
            startActivity(intent)
        }
        binding.btEveryDayThings.setOnClickListener {
            val intent = Intent(this,beginner::class.java)
            intent.putExtra("level", "intermediate")
            startActivity(intent)
        }
        binding.btJobInterview.setOnClickListener {
            val intent = Intent(this,beginner::class.java)
            intent.putExtra("level", "advanced")
            startActivity(intent)
        }
        binding.btBegInterm.setOnClickListener {
            val intent = Intent(this,beginner::class.java)
            intent.putExtra("level", "begInterm")
            startActivity(intent)
        }
        binding.allQuestions .setOnClickListener {
            val intent = Intent(this,beginner::class.java)
            intent.putExtra("level", "allquestions")
            startActivity(intent)
        }
        binding.btSettings.setOnClickListener {
            startActivity(Intent(this,SettingsScreen::class.java))
        }
    }
}