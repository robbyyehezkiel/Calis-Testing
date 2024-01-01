package com.robbyyehezkiel.calisapplication.ui.auth.lesson.letter.fragment

class LetterCompleteFragment : BaseLetterFragment() {
    override fun getLetterQuestionList(): List<Char> {
        return ('A'..'Z').toList()
    }
    override val defaultLetter: Char
        get() = 'A'
}