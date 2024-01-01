package com.robbyyehezkiel.calisapplication.ui.auth.user.add

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.UserRepository

class AddUserViewModelFactory(
    private val application: Application,
    private val repository: UserRepository,
    private val fireStore: FirebaseFirestore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddUserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddUserViewModel(application, repository, fireStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
