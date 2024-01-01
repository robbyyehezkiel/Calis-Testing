package com.robbyyehezkiel.calisapplication.ui.auth.lesson.add.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.robbyyehezkiel.calisapplication.data.model.Question
import com.robbyyehezkiel.calisapplication.databinding.FragmentQuestionEssayBinding

class QuestionEssayFragment : Fragment() {

    private var _binding: FragmentQuestionEssayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionEssayBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun getQuestionEssay(category: String, subcategory: String): Question {
        val questionText = binding.editTextQuestion.text.toString()
        val answerText = binding.editTextAnswer.text.toString()

        return Question(
            category = category,
            subcategory = subcategory,
            question = questionText,
            correctAnswer = answerText
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
