package com.robbyyehezkiel.calisapplication.ui.auth.lesson.add

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.robbyyehezkiel.calisapplication.data.model.Question
import com.robbyyehezkiel.calisapplication.ui.utils.Event
import com.robbyyehezkiel.calisapplication.ui.utils.showSnackBarWithoutAction

class AddQuestionViewModel : ViewModel() {

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> get() = _categories

    private val _subcategories = MutableLiveData<List<String>>()
    val subcategories: LiveData<List<String>> get() = _subcategories

    private val _selectedCategory = MutableLiveData<String?>()
    val selectedCategory: LiveData<String?> get() = _selectedCategory

    private val _selectedSubcategory = MutableLiveData<String?>()
    val selectedSubcategory: LiveData<String?> get() = _selectedSubcategory

    private val _showProgressBar = MutableLiveData<Boolean>()
    val showProgressBar: LiveData<Boolean> get() = _showProgressBar

    private val _errorSnackBar = MutableLiveData<Event<String>>()
    val errorSnackBar: LiveData<Event<String>> get() = _errorSnackBar

    private val _loadQuestionEssayFragment = MutableLiveData<Boolean>()
    val loadQuestionEssayFragment: LiveData<Boolean> get() = _loadQuestionEssayFragment

    private val _loadQuestionOptionFragment = MutableLiveData<Boolean>()
    val loadQuestionOptionFragment: LiveData<Boolean> get() = _loadQuestionOptionFragment

    private val _clearForm = MutableLiveData<Event<Unit>>()
    val clearForm: LiveData<Event<Unit>> get() = _clearForm

    private val _showBlankFragment = MutableLiveData<Boolean>()
    val showBlankFragment: LiveData<Boolean> get() = _showBlankFragment

    private val fireStore = FirebaseFirestore.getInstance()

    companion object {
        private const val ABC_OPTION = "ABC Option"
        private const val ABC_ESSAY = "ABC Essay"
    }

    fun saveQuestionToFireStore(question: Question, rootView: View) {
        val questionData = hashMapOf(
            "category" to question.category,
            "subcategory" to question.subcategory,
            "question" to question.question,
            "optionA" to question.optionA,
            "optionB" to question.optionB,
            "optionC" to question.optionC,
            "optionD" to question.optionD,
            "correctAnswer" to question.correctAnswer
        )

        _showProgressBar.value = true

        fireStore.collection("questions")
            .add(questionData)
            .addOnSuccessListener {
                _showProgressBar.value = false
                handleQuestionSaveSuccess(rootView)
            }
            .addOnFailureListener { exception ->
                _showProgressBar.value = false
                handleFailure(exception)
            }
    }

    private fun handleQuestionSaveSuccess(rootView: View) {
        showSnackBarWithoutAction(rootView, "Question saved successfully !")
        _clearForm.value = Event(Unit)
        _showBlankFragment.value = true
    }

    fun fetchCategories() {
        _showProgressBar.value = true
        fireStore.collection("quiz_category")
            .get()
            .addOnSuccessListener { result ->
                val categoryOptions: List<String> = result.map { it.id }
                if (categoryOptions.isEmpty()) {
                    _errorSnackBar.value = Event("No categories available !")
                } else {
                    _categories.value = categoryOptions
                }
                _showProgressBar.value = false
            }
            .addOnFailureListener { exception ->
                handleFailure(exception)
                _showProgressBar.value = false
            }
    }


    fun fetchAndPopulateSubcategories(category: String?) {
        category?.let {
            _showProgressBar.value = true

            fireStore.collection("quiz_category").document(category)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val subcategories =
                            (document["subCategories"] as? List<*>)?.filterIsInstance<Map<String, String>>()
                        subcategories?.let {
                            val subcategoryNames = subcategories.map { it["name"] ?: "" }
                            _subcategories.value = subcategoryNames
                        }
                    }
                    _showProgressBar.value = false
                }
                .addOnFailureListener { exception ->
                    handleFailure(exception)
                    _showProgressBar.value = false
                }
        }
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSelectedSubcategory(subcategory: String) {
        _selectedSubcategory.value = subcategory

        when (subcategory) {
            ABC_OPTION -> {
                _loadQuestionEssayFragment.value = false
                _loadQuestionOptionFragment.value = true
            }

            ABC_ESSAY -> {
                _loadQuestionEssayFragment.value = true
                _loadQuestionOptionFragment.value = false
            }

            else -> {
                _loadQuestionEssayFragment.value = false
                _loadQuestionOptionFragment.value = false
                _showBlankFragment.value = true
            }
        }
    }

    private fun handleFailure(exception: Exception) {
        _errorSnackBar.value = Event("An error occurred: ${exception.message}")
    }

    fun resetFormState() {
        _selectedCategory.value = null
        _selectedSubcategory.value = null
        _showBlankFragment.value = false
    }
}
