package com.robbyyehezkiel.calisapplication.ui.non_auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.SettingPreferences
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.UserRepository
import com.robbyyehezkiel.calisapplication.ui.auth.user.utils.dataStore
import com.robbyyehezkiel.calisapplication.ui.utils.showSnackBarWithoutAction
import kotlinx.coroutines.flow.firstOrNull

abstract class BaseAuthActivity : AppCompatActivity() {

    protected lateinit var auth: FirebaseAuth
    lateinit var userRepository: UserRepository
    protected lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        userRepository = UserRepository(SettingPreferences.getInstance(application.dataStore))
    }

    protected suspend fun isUserDataAvailableInDataStore(): Boolean {
        val user = userRepository.getUserSettingsFlow().firstOrNull()
        return user != null && user.userId.isNotBlank()
    }

    protected suspend fun handleUserDataAvailability(
        userEmail: String?,
        navigateToUserSetting: Class<out AppCompatActivity>,
        navigateToAddUser: Class<out AppCompatActivity>
    ) {
        if (userEmail != null) {
            try {
                val isUserDataAvailable = userRepository.getUsersByEmail(userEmail).isNotEmpty()
                if (isUserDataAvailable) {
                    navigateTo(navigateToUserSetting)
                } else {
                    navigateTo(navigateToAddUser)
                }
            } catch (exception: FirebaseFirestoreException) {
                handleError(
                    "Error fetching user data from FireStore: ${exception.message}",
                    exception
                )
            }
        }
    }

    protected fun handleError(message: String, exception: Exception? = null) {
        Log.e(javaClass.simpleName, message, exception)
        showSnackBarWithoutAction(findViewById(android.R.id.content), message)
    }

    protected fun navigateTo(activityClass: Class<out AppCompatActivity>) {
        startActivity(Intent(this, activityClass))
        finishAffinity()
    }
}