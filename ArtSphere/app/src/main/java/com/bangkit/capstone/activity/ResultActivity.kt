package com.bangkit.capstone.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.capstone.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val label = intent.getStringExtra("LABEL")
        binding.userDescription.text = label

        setupButton()
    }

    private fun setupButton() {
        binding.resetButton.setOnClickListener {
            val resetIntent = Intent(this@ResultActivity, ScanActivity::class.java)
            startActivity(resetIntent)
            finish()
        }
    }
}
