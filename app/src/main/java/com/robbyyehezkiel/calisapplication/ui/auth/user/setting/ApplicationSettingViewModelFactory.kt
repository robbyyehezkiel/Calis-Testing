package com.robbyyehezkiel.calisapplication.ui.auth.user.setting


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.UserRepository

class ApplicationSettingViewModelFactory(private val userRepository: UserRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApplicationSettingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ApplicationSettingViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
