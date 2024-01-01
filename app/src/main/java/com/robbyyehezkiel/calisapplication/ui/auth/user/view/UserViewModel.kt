package com.robbyyehezkiel.calisapplication.ui.auth.user.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.SettingPreferences
import com.robbyyehezkiel.calisapplication.data.model.User
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.UserRepository
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.dataStore
import com.robbyyehezkiel.calisapplication.ui.utils.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository =
        UserRepository(SettingPreferences.getInstance(application.dataStore))

    private val _usersLiveData = MutableLiveData<List<User>>()
    val usersLiveData: LiveData<List<User>> = _usersLiveData

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> = _loadingState

    private val auth = FirebaseAuth.getInstance()

    fun fetchUserByEmail() {
        _loadingState.value = LoadingState.Loading

        val userEmail = auth.currentUser?.email
        if (userEmail != null) {
            viewModelScope.launch {
                try {
                    val users = withContext(Dispatchers.IO) {
                        repository.getUsersByEmail(userEmail)
                    }
                    _usersLiveData.value = users
                    _loadingState.value = LoadingState.Success
                } catch (exception: Exception) {
                    handleException(exception, "Failed to fetch user")
                }
            }
        } else {
            _loadingState.value = LoadingState.Error("User email not available")
        }
    }

    fun saveUserSettings(user: User) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.saveUserSettings(user)
                }
            } catch (exception: Exception) {
                handleException(exception, "Failed to save user settings")
            }
        }
    }

    private fun handleException(exception: Exception, message: String) {
        _loadingState.value = LoadingState.Error("$message: ${exception.message}")
    }
}
