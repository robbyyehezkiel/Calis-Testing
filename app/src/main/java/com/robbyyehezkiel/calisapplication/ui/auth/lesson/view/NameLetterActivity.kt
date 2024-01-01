package com.robbyyehezkiel.calisapplication.ui.auth.lesson.view

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.robbyyehezkiel.calisapplication.R
import com.robbyyehezkiel.calisapplication.databinding.ActivityNameLetterBinding
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.UserRepository
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.SettingPreferences
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.dataStore
import com.robbyyehezkiel.calisapplication.ui.auth.lesson.utils.ClickableTextView
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

class NameLetterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNameLetterBinding
    private lateinit var userRepository: UserRepository
    private val letterViews = mutableListOf<TextView>()
    private val matchedIndices = mutableSetOf<Int>()
    private lateinit var originalText: String
    private val targetText = SpannableStringBuilder()
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNameLetterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rootView = binding.root
        binding.startAnimationButton.setOnClickListener {
            startAnimation()
        }

        val settingPreferences = SettingPreferences.getInstance(application.dataStore)
        userRepository = UserRepository(settingPreferences)

        lifecycleScope.launch {
            userRepository.getUserSettingsFlow().collect { userSettings ->
                originalText = userSettings.name

                targetText.clear()
                for (i in originalText.indices) {
                    targetText.append(originalText[i].toString())
                    targetText.setSpan(
                        ForegroundColorSpan(Color.RED),
                        i,
                        i + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                binding.quizLetterTextView.text = targetText
            }
        }

        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val locale = Locale("id", "ID") // Indonesian locale
                val result = textToSpeech.setLanguage(locale)

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    showSnackBar(getString(R.string.tts_language_not_available))
                }
            } else {
                showSnackBar(getString(R.string.tts_init_failed, status))
            }
        }
    }

    private fun startAnimation() {
        val textSize = binding.quizLetterTextView.textSize
        binding.quizLetterTextView.setTextColor(Color.RED)
        letterViews.clear()
        matchedIndices.clear()

        val parent = binding.quizLetterTextView.parent as ViewGroup
        parent.removeViews(1, parent.childCount - 1)

        val edgePaddingDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            32f,
            resources.displayMetrics
        )
        val minDistanceDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            16f,
            resources.displayMetrics
        )

        val targetX = binding.quizLetterTextView.x
        val targetY = binding.quizLetterTextView.y
        val targetWidth = binding.quizLetterTextView.width
        val targetHeight = binding.quizLetterTextView.height

        val positions = mutableListOf<Pair<Float, Float>>()

        for (letter in originalText) {
            var randomX: Float
            var randomY: Float

            do {
                randomX = Random.nextInt(
                    edgePaddingDp.toInt() + targetWidth / 2,
                    parent.width - edgePaddingDp.toInt() - targetWidth / 2
                ).toFloat()
                randomY = Random.nextInt(
                    edgePaddingDp.toInt() + targetHeight / 2,
                    parent.height - edgePaddingDp.toInt() - targetHeight / 2
                ).toFloat()
            } while (positions.any { (x, y) ->
                    val distanceX = abs(randomX - x)
                    val distanceY = abs(randomY - y)
                    val distance = sqrt((distanceX * distanceX + distanceY * distanceY).toDouble())
                    distance < minDistanceDp
                })

            positions.add(Pair(randomX, randomY))

            val distanceX = abs(randomX - targetX)
            val distanceY = abs(randomY - targetY)
            val distanceFromTarget =
                sqrt((distanceX * distanceX + distanceY * distanceY).toDouble())

            val scaleFactor = minDistanceDp / distanceFromTarget.toFloat()
            val adjustedRandomX = ((randomX - targetX) * scaleFactor) + targetX
            val adjustedRandomY = ((randomY - targetY) * scaleFactor) + targetY

            val letterView = ClickableTextView(this) // Use the custom ClickableTextView
            letterView.text = letter.toString()
            letterView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            letterView.setTextColor(Color.BLACK)
            letterView.x = adjustedRandomX
            letterView.y = adjustedRandomY
            parent.addView(letterView)
            letterViews.add(letterView)

            val animator = ValueAnimator.ofFloat(0f, 1f)
            animator.duration = 500 // Animation duration in milliseconds
            animator.interpolator = AccelerateDecelerateInterpolator()

            animator.addUpdateListener { valueAnimator ->
                val progress = valueAnimator.animatedValue as Float
                val interpolatedX =
                    (1 - progress) * adjustedRandomX + progress * randomX
                val interpolatedY =
                    (1 - progress) * adjustedRandomY + progress * randomY

                letterView.x = interpolatedX
                letterView.y = interpolatedY
            }

            animator.start()

            letterView.setOnTouchListener(object : View.OnTouchListener {
                private var dx = 0f
                private var dy = 0f

                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    when (event?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            dx = v!!.x - event.rawX
                            dy = v.y - event.rawY
                        }

                        MotionEvent.ACTION_MOVE -> {
                            v!!.animate()
                                .x(event.rawX + dx)
                                .y(event.rawY + dy)
                                .setDuration(0)
                                .start()
                        }

                        MotionEvent.ACTION_UP -> {
                            checkPosition(letterView, binding.quizLetterTextView)
                        }
                    }
                    return true
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release TextToSpeech resources
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

    private fun checkPosition(letterView: View, targetView: TextView) {
        if (isViewOverlapping(letterView, targetView)) {
            val letter = (letterView as TextView).text[0]

            var foundIndex = -1
            for (i in originalText.indices) {
                if (originalText[i] == letter && i !in matchedIndices) {
                    foundIndex = i
                    break
                }
            }

            if (foundIndex != -1) {
                matchedIndices.add(foundIndex)
                targetText.setSpan(
                    ForegroundColorSpan(Color.GREEN),
                    foundIndex,
                    foundIndex + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                targetView.text = targetText
                letterView.isEnabled = false

                // Make the successfully dragged letter invisible or gone
                letterView.visibility = View.INVISIBLE // or View.GONE

                // Read the matched letter using TextToSpeech
                val utteranceId = "${System.currentTimeMillis()}"
                val params = Bundle()
                params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
                textToSpeech.speak(letter.toString(), TextToSpeech.QUEUE_FLUSH, params, utteranceId)

                // Optionally, you can remove the view from its parent if you don't need it anymore
                val parent = letterView.parent as ViewGroup
                parent.removeView(letterView)
            }
        }
    }


    private fun isViewOverlapping(view1: View, view2: View): Boolean {
        val rect1 = IntArray(2)
        view1.getLocationOnScreen(rect1)
        val rect2 = IntArray(2)
        view2.getLocationOnScreen(rect2)

        return !(rect1[0] > rect2[0] + view2.width ||
                rect1[0] + view1.width < rect2[0] ||
                rect1[1] > rect2[1] + view2.height ||
                rect1[1] + view1.height < rect2[1])
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}