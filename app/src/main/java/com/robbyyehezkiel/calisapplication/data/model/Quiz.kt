package com.robbyyehezkiel.calisapplication.data.model

data class Quiz(
    val category: String,
    val subCategories: List<SubCategory>
)
data class SubCategory(
    val name: String
)

data class Question(
    var category: String = "",
    var subcategory: String = "",
    val question: String = "",
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val correctAnswer: String = "",
    val userAnswer: String = ""
)
