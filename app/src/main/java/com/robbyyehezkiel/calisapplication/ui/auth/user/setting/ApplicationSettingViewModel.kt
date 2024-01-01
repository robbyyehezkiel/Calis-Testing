package com.robbyyehezkiel.calisapplication.ui.auth.user.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robbyyehezkiel.calisapplication.data.model.User
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.UserRepository
import com.robbyyehezkiel.calisapplication.ui.utils.LoadingState
import kotlinx.coroutines.launch

class ApplicationSettingViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> = _loadingState

    private val _showSuccessMessage = MutableLiveData<Unit>()
    val showSuccessMessage: LiveData<Unit> = _showSuccessMessage

    fun updateUserProfile(updatedUser: User) {
        _loadingState.value = LoadingState.Loading

        viewModelScope.launch {
            try {
                userRepository.saveUserSettings(updatedUser)
                userRepository.updateUserInFireStore(updatedUser)
                _loadingState.value = LoadingState.Success

                notifySuccess()
            } catch (e: Exception) {
                handleError("Failed to update user profile: ${e.message}")
            }
        }
    }

    private fun handleError(errorMessage: String) {
        _loadingState.value = LoadingState.Error(errorMessage)
    }

    private fun notifySuccess() {
        _showSuccessMessage.value = Unit
    }
}
