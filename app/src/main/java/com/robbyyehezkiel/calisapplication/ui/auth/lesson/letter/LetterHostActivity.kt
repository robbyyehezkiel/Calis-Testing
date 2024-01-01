package com.robbyyehezkiel.calisapplication.ui.auth.lesson.letter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.robbyyehezkiel.calisapplication.R
import com.robbyyehezkiel.calisapplication.databinding.ActivityLetterHostBinding
import com.robbyyehezkiel.calisapplication.ui.auth.lesson.letter.fragment.LetterCompleteFragment
import com.robbyyehezkiel.calisapplication.ui.auth.lesson.letter.fragment.LetterConsonantFragment
import com.robbyyehezkiel.calisapplication.ui.auth.lesson.letter.fragment.LetterVocalsFragment
import com.robbyyehezkiel.calisapplication.ui.utils.CustomTextToSpeech
import com.robbyyehezkiel.calisapplication.ui.utils.showSnackBarWithoutAction

class LetterHostActivity : AppCompatActivity() {

    private lateinit var customTextToSpeech: CustomTextToSpeech
    private lateinit var binding: ActivityLetterHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLetterHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backward.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        customTextToSpeech = CustomTextToSpeech(this)

        when (intent.getStringExtra("activity_type")) {
            "complete" -> supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, LetterCompleteFragment())
                .commit()

            "vocals" -> supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, LetterVocalsFragment())
                .commit()

            "consonants" -> supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, LetterConsonantFragment())
                .commit()

            else -> {
                showSnackBarWithoutAction(binding.root, getString(R.string.hello_blank_fragment))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::customTextToSpeech.isInitialized) {
            customTextToSpeech.shutdown()
        }
    }
}
