package com.robbyyehezkiel.calisapplication.ui.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.lifecycle.Observer

fun showSnackBarWithoutAction(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
}

sealed class LoadingState {
    object Loading : LoadingState()
    object Success : LoadingState()
    data class Error(val message: String) : LoadingState()
}

class Event<out T>(private val content: T) {

    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}