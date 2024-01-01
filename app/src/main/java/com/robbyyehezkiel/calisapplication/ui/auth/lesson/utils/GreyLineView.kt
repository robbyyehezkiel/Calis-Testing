package com.robbyyehezkiel.calisapplication.ui.auth.lesson.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class GreyLineView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val paint = Paint()

    init {
        paint.color =
            ContextCompat.getColor(context, android.R.color.darker_gray) // Change color as needed
        paint.strokeWidth = 5f // Adjust line width as needed
    }

    fun connect(startView: View, endView: View) {
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                viewTreeObserver.removeOnPreDrawListener(this)

                val startLocation = IntArray(2)
                startView.getLocationOnScreen(startLocation)

                val endLocation = IntArray(2)
                endView.getLocationOnScreen(endLocation)

                val startX = startLocation[0] + startView.width / 2
                val startY = startLocation[1] + startView.height / 2

                val endX = endLocation[0] + endView.width / 2
                val endY = endLocation[1] + endView.height / 2

                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT
                )
                setWillNotDraw(false)

                layoutParams.width = (endX - startX)
                layoutParams.height = (endY - startY)
                translationX = startX.toFloat()
                translationY = startY.toFloat()

                invalidate()
                return true
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLine(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }
}