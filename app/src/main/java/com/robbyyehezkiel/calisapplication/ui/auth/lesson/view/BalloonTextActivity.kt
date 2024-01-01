package com.robbyyehezkiel.calisapplication.ui.auth.lesson.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.robbyyehezkiel.calisapplication.R
import com.robbyyehezkiel.calisapplication.databinding.ActivityBalloonTextBinding
import java.util.Locale
import kotlin.random.Random

class BalloonTextActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityBalloonTextBinding
    private lateinit var letterTextViews: List<TextView>
    private lateinit var rootView: View
    private var score = 0
    private val animationDuration = 10000L
    private val animationDelay = 1000L
    private var quizLetter: String = ""
    private val clickedIndices = mutableSetOf<Int>()
    private var animationsCompleted = 0
    private lateinit var textToSpeech: TextToSpeech
    private val animators = mutableMapOf<TextView, ObjectAnimator>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBalloonTextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rootView = binding.root
        textToSpeech = TextToSpeech(this, this)

        initializeUI()
    }

    private fun initializeUI() {
        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        letterTextViews = listOf(
            binding.letter0, binding.letter1, binding.letter2, binding.letter3,
            binding.letter4, binding.letter5, binding.letter6, binding.letter7,
            binding.letter8, binding.letter9
        )
    }

    private fun setupListeners() {
        binding.backward.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.startButton.setOnClickListener {
            resetAnimationsAndQuiz()
            generateAndSetRandomLetter()
            startAnimations()
        }

        letterTextViews.forEach { textView ->
            textView.visibility = View.INVISIBLE
            textView.setOnClickListener {
                handleLetterClick(textView)
            }
        }
    }

    private fun handleLetterClick(textView: TextView) {
        val clickedIndex = letterTextViews.indexOf(textView)
        if (!clickedIndices.contains(clickedIndex)) {
            animators[textView]?.cancel()

            val isCorrect = textView.text == quizLetter
            if (isCorrect) {
                updateScore(100)
                applyCorrectClickAnimation(textView)
            } else {
                updateScore(-50)
                applyIncorrectClickAnimation(textView)
            }
            clickedIndices.add(clickedIndex)
            applyClickAnimation(textView)

            speakIndonesian(textView.text.toString())
        }
    }

    private fun speakIndonesian(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

    private fun applyCorrectClickAnimation(view: View) {
        view.setBackgroundResource(R.drawable.circle_correct_shape)
    }

    private fun applyIncorrectClickAnimation(view: View) {
        view.setBackgroundResource(R.drawable.circle_incorrect_shape)
    }

    private fun applyClickAnimation(view: View) {
        val scaleUpAnim = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.2f),
            PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.2f)
        ).apply {
            duration = 150
            repeatMode = ValueAnimator.REVERSE
            repeatCount = 1
        }

        val sparkleAnim = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.0f).apply {
            duration = 400
            repeatMode = ValueAnimator.REVERSE
            repeatCount = 3
        }

        sparkleAnim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                // Leave empty or perform any necessary actions on animation repeat
            }

            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator) {
                // Leave empty or perform any necessary actions on animation repeat
            }

            override fun onAnimationRepeat(animation: Animator) {
                // Leave empty or perform any necessary actions on animation repeat
            }
        })

        scaleUpAnim.start()
        sparkleAnim.start()
    }

    private fun updateScore(points: Int) {
        score += points
        binding.score.text = score.toString()

        if (score < 0) {
            showGameOverSnackBar()
        }
    }

    private fun showGameOverSnackBar() {
        Snackbar.make(rootView, getString(R.string.game_over), Snackbar.LENGTH_LONG).show()
    }

    private fun resetAnimationsAndQuiz() {
        letterTextViews.forEach { textView ->
            textView.clearAnimation()
            textView.visibility = View.INVISIBLE
            applyDefaultClickAnimation(textView)
        }
        clickedIndices.clear()
    }

    private fun generateAndSetRandomLetter() {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        quizLetter = alphabet.random().toString()
        setQuizQuestion(quizLetter)

        val sameLetterIndices = mutableSetOf<Int>()
        while (sameLetterIndices.size < 3) {
            val index = Random.nextInt(10)
            if (index !in sameLetterIndices) {
                sameLetterIndices.add(index)
            }
        }

        val remainingIndices = (0..9).filterNot { it in sameLetterIndices }
        for (index in remainingIndices) {
            letterTextViews[index].text = generateRandomDifferentLetter(quizLetter)
        }
        for (index in sameLetterIndices) {
            letterTextViews[index].text = quizLetter
        }
    }

    private fun setQuizQuestion(letter: String) {
        binding.quizQuestion.text = letter
    }

    private fun generateRandomDifferentLetter(exclude: String): String {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val filteredAlphabet = alphabet.filter { it != exclude[0] }
        return filteredAlphabet.random().toString()
    }

    private fun startAnimations() {
        animationsCompleted = 0

        binding.startButton.visibility = View.INVISIBLE

        letterTextViews.forEachIndexed { index, textView ->
            val delay = if (index > 0) index * animationDelay else 0
            Handler(mainLooper).postDelayed({
                textView.translationY = textView.height.toFloat()
                textView.visibility = View.VISIBLE
                startAnimation(textView)
                animationsCompleted++

                if (animationsCompleted == letterTextViews.size) {
                    Handler(mainLooper).postDelayed({
                        showScoreDialog()
                        binding.startButton.visibility = View.VISIBLE
                    }, animationDuration)
                }
            }, delay)
        }
    }

    private fun showScoreDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(getString(R.string.your_score, score))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }

    private fun startAnimation(textView: TextView) {
        val screenWidth = resources.displayMetrics.widthPixels
        val randomX = Random.nextInt(screenWidth - textView.width)

        textView.x = randomX.toFloat()

        val animator = ObjectAnimator.ofFloat(
            textView,
            "translationY",
            textView.y,
            -textView.height.toFloat() - textView.top.toFloat()
        ).apply {
            duration = animationDuration
            start()
        }

        animators[textView] = animator
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale("id", "ID"))

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                showSnackBar(getString(R.string.tts_language_not_available))
            }
        } else {
            showSnackBar(getString(R.string.tts_init_failed, status))
        }
    }

    private fun applyDefaultClickAnimation(view: View) {
        view.setBackgroundResource(R.drawable.circle_background) // Set default background
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}