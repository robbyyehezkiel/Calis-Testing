package com.robbyyehezkiel.calisapplication.ui.auth.lesson.letter.fragment

class LetterConsonantFragment : BaseLetterFragment() {

    override fun getLetterQuestionList(): List<Char> {
        return ('A'..'Z').filter { it !in listOf('A', 'I', 'U', 'E', 'O') }
    }

    override val defaultLetter: Char
        get() = 'B'
}