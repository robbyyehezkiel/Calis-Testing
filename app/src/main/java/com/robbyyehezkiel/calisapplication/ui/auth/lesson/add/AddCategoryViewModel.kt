package com.robbyyehezkiel.calisapplication.ui.auth.lesson.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.robbyyehezkiel.calisapplication.data.model.Quiz
import com.robbyyehezkiel.calisapplication.ui.utils.LoadingState

class AddCategoryViewModel : ViewModel() {
    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> get() = _loadingState

    private val fireStore = FirebaseFirestore.getInstance()
    private val quizCollection = fireStore.collection("quiz_category")

    fun addOrUpdateCategory(quiz: Quiz) {
        _loadingState.value = LoadingState.Loading

        val categoryDocument = quizCollection.document(quiz.category)

        categoryDocument.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result

                if (document != null && document.exists()) {
                    updateCategory(categoryDocument, quiz)
                } else {
                    addNewCategory(categoryDocument, quiz)
                }
            } else {
                _loadingState.value =
                    LoadingState.Error("Error checking for existing category: ${task.exception?.message}")
            }
        }
    }

    private fun updateCategory(categoryDocument: DocumentReference, quiz: Quiz) {
        categoryDocument.update(
            "subCategories",
            FieldValue.arrayUnion(*quiz.subCategories.toTypedArray())
        )
            .addOnSuccessListener {
                _loadingState.value = LoadingState.Success
            }
            .addOnFailureListener { e ->
                _loadingState.value = LoadingState.Error("Error updating category: $e")
            }
    }

    private fun addNewCategory(categoryDocument: DocumentReference, quiz: Quiz) {
        categoryDocument.set(quiz)
            .addOnSuccessListener {
                _loadingState.value = LoadingState.Success
            }
            .addOnFailureListener { e ->
                _loadingState.value = LoadingState.Error("Error adding new category: $e")
            }
    }
}
