package com.robbyyehezkiel.calisapplication.ui.auth.lesson.letter.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.robbyyehezkiel.calisapplication.R
import com.robbyyehezkiel.calisapplication.databinding.FragmentLetterBaseBinding
import com.robbyyehezkiel.calisapplication.ui.utils.CustomTextToSpeech
import java.util.Locale

abstract class BaseLetterFragment : Fragment() {

    protected lateinit var binding: FragmentLetterBaseBinding
    private lateinit var customTextToSpeech: CustomTextToSpeech
    private var currentLetter: Char = 'A'
    protected abstract val defaultLetter: Char
    private var isUpperCase: Boolean = true
    private var handler = Handler(Looper.getMainLooper())
    private var autoTextRunning = false
    private var isAnimating = false
    private lateinit var letterQuestionAnimator: ValueAnimator

    companion object {
        private const val AUTO_TEXT_DELAY_MS = 2000L
        private const val ANIMATION_DURATION = 300
        private const val INITIAL_ANIMATION_DURATION = 3000
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLetterBaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customTextToSpeech = CustomTextToSpeech(requireContext(), Locale.getDefault().language)
        setupUI()
        startInitialLetterQuestionAnimation()
        currentLetter = defaultLetter
        updateLetter()
    }

    abstract fun getLetterQuestionList(): List<Char>

    private fun setupUI() {
        with(binding) {
            arrowLeft.setOnClickListener { navigateLetter(false) }
            arrowRight.setOnClickListener { navigateLetter(true) }
            letterQuestion.setOnClickListener {
                animateLetterQuestion()
                speakCurrentLetter()
            }

            autoTextButton.setOnClickListener { toggleAutoText() }
            uppercaseLowercaseButton.setOnClickListener { toggleCase() }

            val soundImageView: ImageView = requireView().findViewById(R.id.sound)
            soundImageView.setOnClickListener {
                animateLetterQuestion()
                speakCurrentLetter()
            }
        }
    }

    private fun toggleCase() {
        isUpperCase = !isUpperCase
        updateLetter()
        speakCurrentLetter()
    }

    private fun toggleAutoText() {
        if (autoTextRunning) {
            stopAutoText()
        } else {
            startAutoText()
        }
    }

    private fun startAutoText() {
        autoTextRunning = true
        with(binding.autoTextButton) {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_backward, 0)
        }
        updateArrowVisibility(View.INVISIBLE)
        handler.postDelayed(autoTextUpdateRunnable, AUTO_TEXT_DELAY_MS)
    }

    private fun stopAutoText() {
        autoTextRunning = false
        with(binding.autoTextButton) {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_forward, 0)
        }
        updateArrowVisibility(View.VISIBLE)
        handler.removeCallbacks(autoTextUpdateRunnable)
    }

    private fun updateArrowVisibility(visibility: Int) {
        binding.arrowLeft.visibility = visibility
        binding.arrowRight.visibility = visibility
    }

    private fun animateLetterQuestion() {
        val scaleX = ObjectAnimator.ofFloat(binding.letterQuestion, View.SCALE_X, 1.0f, 1.2f, 1.0f)
        val scaleY = ObjectAnimator.ofFloat(binding.letterQuestion, View.SCALE_Y, 1.0f, 1.2f, 1.0f)

        val animatorSet = AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            duration = ANIMATION_DURATION.toLong()
            interpolator = AccelerateDecelerateInterpolator()
        }

        animatorSet.start()
    }

    private fun startInitialLetterQuestionAnimation() {
        letterQuestionAnimator = ValueAnimator.ofFloat(0f, 2f, 0f, -2f, 0f).apply {
            addUpdateListener { valueAnimator ->
                val animatedValue = valueAnimator.animatedValue as Float
                binding.letterQuestion.rotation = animatedValue
            }
            duration = INITIAL_ANIMATION_DURATION.toLong()
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = ValueAnimator.INFINITE
            start()
        }
        isAnimating = true
    }


    private fun stopLetterQuestionAnimation() {
        if (isAnimating) {
            letterQuestionAnimator.cancel()
            isAnimating = false
        }
    }

    private val autoTextUpdateRunnable: Runnable = Runnable {
        run {
            navigateLetter(true)
            handler.postDelayed(autoTextUpdateRunnable, AUTO_TEXT_DELAY_MS)
        }
    }

    private fun speakCurrentLetter() {
        val updatedLetter =
            if (isUpperCase) currentLetter.uppercaseChar() else currentLetter.lowercaseChar()
        customTextToSpeech.speak(updatedLetter.toString())
    }

    private fun updateLetter() {
        val updatedLetter =
            if (isUpperCase) currentLetter.uppercaseChar() else currentLetter.lowercaseChar()
        with(binding) {
            letterQuestion.text = updatedLetter.toString()
            uppercaseLowercaseButton.text =
                if (isUpperCase) getString(R.string.button_text_uppercase) else getString(R.string.button_text_lowercase)
        }
    }

    private fun navigateLetter(isNext: Boolean) {
        val letterQuestionList = getLetterQuestionList()

        val currentIndex = letterQuestionList.indexOf(currentLetter)
        currentLetter = if (isNext) {
            if (currentIndex < letterQuestionList.size - 1) {
                letterQuestionList[currentIndex + 1]
            } else {
                letterQuestionList[0]
            }
        } else {
            if (currentIndex > 0) {
                letterQuestionList[currentIndex - 1]
            } else {
                letterQuestionList[letterQuestionList.size - 1]
            }
        }

        updateLetter()
        speakCurrentLetter()
        animateLetterQuestion()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAutoText()
        customTextToSpeech.shutdown()
        stopLetterQuestionAnimation()
    }
}