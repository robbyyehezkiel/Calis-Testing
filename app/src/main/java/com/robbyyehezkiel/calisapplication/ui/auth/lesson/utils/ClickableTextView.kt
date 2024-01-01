package com.robbyyehezkiel.calisapplication.ui.auth.lesson.utils

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class ClickableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var touchListener: OnTouchListener? = null

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun setOnTouchListener(listener: OnTouchListener?) {
        touchListener = listener
        super.setOnTouchListener(listener)
    }
}