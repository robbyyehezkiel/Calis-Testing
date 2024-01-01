package com.robbyyehezkiel.calisapplication.ui.auth.user.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.robbyyehezkiel.calisapplication.data.model.User
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.UserRepository
import com.robbyyehezkiel.calisapplication.ui.utils.LoadingState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddUserViewModel(
    application: Application,
    private val repository: UserRepository,
    private val fireStore: FirebaseFirestore
) : AndroidViewModel(application) {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> = _loadingState

    private val _snackBarMessage = MutableLiveData<String?>()
    val snackBarMessage: LiveData<String?> = _snackBarMessage

    private val _userAddedSuccessfully = MutableLiveData<Boolean>()
    val userAddedSuccessfully: LiveData<Boolean>
        get() = _userAddedSuccessfully

    fun addUserToFireStoreAndSaveSettings(
        user: User,
    ) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading

            try {
                fireStore.collection("users").document(user.userId).set(user).await()
                _loadingState.value = LoadingState.Success
                _snackBarMessage.value = "User added successfully"
                repository.saveUserSettings(user)
                _userAddedSuccessfully.value = true
            } catch (e: Exception) {
                _loadingState.value = LoadingState.Error("Failed to add user: ${e.message}")
            }
        }
    }

    fun createUser(name: String, age: Int, email: String): User {
        val newUserId = generateNewUserId()
        return User(userId = newUserId, name = name, age = age, email = email)
    }

    private fun generateNewUserId(): String {
        return FirebaseFirestore.getInstance().collection("users").document().id
    }

}