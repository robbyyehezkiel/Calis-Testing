package com.robbyyehezkiel.calisapplication.ui.auth.user.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.robbyyehezkiel.calisapplication.data.model.User
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class UserRepository(private val settingPreferences: SettingPreferences) {

    private val fireStore = FirebaseFirestore.getInstance()

    suspend fun getUsersByEmail(email: String): List<User> {
        val users = mutableListOf<User>()

        try {
            val querySnapshot = fireStore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()

            for (document in querySnapshot.documents) {
                document.toObject(User::class.java)?.let {
                    users.add(it)
                }
            }
        } catch (exception: Exception) {
            handleException(exception, "Error fetching user data from FireStore")
        }

        return users
    }

    fun getUserSettingsFlow() = settingPreferences.getUserSettingsFlow()

    suspend fun saveUserSettings(user: User) {
        settingPreferences.saveUserSettings(user)
    }

    suspend fun updateUserInFireStore(user: User) {
        try {
            val userRef = fireStore.collection("users").document(user.userId)
            val userData = mapOf(
                "name" to user.name,
                "age" to user.age,
                "email" to user.email
            )

            userRef.update(userData).await()

            println("User data updated successfully.")
        } catch (exception: Exception) {
            handleException(exception, "Failed to update user data")
        }
    }

    suspend fun clearUserSettings() {
        settingPreferences.clearUserSettings()
    }

    private fun handleException(exception: Exception, message: String) {
        println("$message: ${exception.message}")
    }
}
