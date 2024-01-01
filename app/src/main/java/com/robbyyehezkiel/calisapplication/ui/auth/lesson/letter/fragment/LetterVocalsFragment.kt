package com.robbyyehezkiel.calisapplication.ui.auth.lesson.letter.fragment

class LetterVocalsFragment : BaseLetterFragment() {

    override fun getLetterQuestionList(): List<Char> {
        return listOf('A', 'I', 'U', 'E', 'O')
    }

    override val defaultLetter: Char
        get() = 'A'
}
