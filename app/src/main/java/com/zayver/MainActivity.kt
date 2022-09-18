package com.zayver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zayver.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }
    private fun setListeners(){
        binding.readJsonButton.setOnClickListener {
            val int = Intent(baseContext,ReadJsonActivity::class.java)
            startActivity(int)
        }
        binding.readContactsButton.setOnClickListener {
            val int = Intent(baseContext, ReadContactsActivity::class.java)
            startActivity(int)
        }
        binding.localizationOnceButton.setOnClickListener {
            val int = Intent(baseContext, LocalizationOnceActivity::class.java)
            startActivity(int)
        }
        binding.localizationSuscriptionButton.setOnClickListener {
            val int = Intent(baseContext, LocalizationSubscriptionActivity::class.java)
            startActivity(int)
        }
        binding.exercise3Button.setOnClickListener {
            val int = Intent(baseContext, Exercise3Activity::class.java)
            startActivity(int)
        }
    }
}