package com.robbyyehezkiel.calisapplication.ui.auth.lesson.add.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.robbyyehezkiel.calisapplication.data.model.Question
import com.robbyyehezkiel.calisapplication.databinding.FragmentQuestionOptionBinding

class QuestionOptionFragment : Fragment() {

    private var _binding: FragmentQuestionOptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionOptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun getQuestionOption(category: String, subcategory: String): Question {
        val questionText = binding.editTextQuestion.text.toString()
        val optionAText = binding.editTextOptionA.text.toString()
        val optionBText = binding.editTextOptionB.text.toString()
        val optionCText = binding.editTextOptionC.text.toString()
        val optionDText = binding.editTextOptionD.text.toString()
        val correctAnswer = binding.editTextOptionCorrectAnswer.text.toString()

        return Question(
            category = category,
            subcategory = subcategory,
            question = questionText,
            optionA = optionAText,
            optionB = optionBText,
            optionC = optionCText,
            optionD = optionDText,
            correctAnswer = correctAnswer
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
