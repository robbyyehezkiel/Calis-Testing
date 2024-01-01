package com.robbyyehezkiel.calisapplication.ui.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

class CustomTextToSpeech(context: Context, language: String = Locale.getDefault().language) {

    private var textToSpeech: TextToSpeech = TextToSpeech(context) { status ->
        if (status == TextToSpeech.SUCCESS) {
            setLanguage(language)
        }
    }

    private fun setLanguage(language: String) {
        val locale = Locale(language)
        textToSpeech.language = locale
    }

    fun speak(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun shutdown() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}
