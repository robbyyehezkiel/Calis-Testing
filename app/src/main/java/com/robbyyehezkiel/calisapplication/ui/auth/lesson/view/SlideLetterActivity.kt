package com.robbyyehezkiel.calisapplication.ui.auth.lesson.view

import android.os.Bundle
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.robbyyehezkiel.calisapplication.databinding.ActivitySlideLetterBinding
import com.robbyyehezkiel.calisapplication.ui.auth.lesson.utils.GreyLineView

class SlideLetterActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySlideLetterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlideLetterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Draw a line connecting startPoint and endPoint
        val greyLine = GreyLineView(this)
        binding.root.addView(greyLine)
        greyLine.connect(binding.startPoint, binding.endPoint)

        // Implement animation to slide from startPoint to endPoint
        val animation = TranslateAnimation(0f, 0f, 0f, binding.endPoint.y - binding.startPoint.y)
        animation.duration = 1000 // Adjust the duration as needed
        binding.startPoint.startAnimation(animation)
    }
}
