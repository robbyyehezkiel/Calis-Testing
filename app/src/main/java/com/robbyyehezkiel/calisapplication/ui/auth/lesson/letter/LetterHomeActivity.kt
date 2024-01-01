package com.robbyyehezkiel.calisapplication.ui.auth.lesson.letter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.robbyyehezkiel.calisapplication.databinding.ActivityLetterHomeBinding

class LetterHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLetterHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLetterHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navigateAlphabet.setOnClickListener {startCompleteHost() }
        binding.navigateVocal.setOnClickListener { startVocalsHost() }
        binding.navigateConsonant.setOnClickListener { startConsonantsHost() }
        binding.btnHome.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }
    private fun startCompleteHost() {
        val intent = Intent(this@LetterHomeActivity, LetterHostActivity::class.java)
        intent.putExtra("activity_type", "complete")
        startActivity(intent)
    }

    private fun startVocalsHost() {
        val intent = Intent(this@LetterHomeActivity, LetterHostActivity::class.java)
        intent.putExtra("activity_type", "vocals")
        startActivity(intent)
    }

    private fun startConsonantsHost() {
        val intent = Intent(this@LetterHomeActivity, LetterHostActivity::class.java)
        intent.putExtra("activity_type", "consonants")
        startActivity(intent)
    }
}
