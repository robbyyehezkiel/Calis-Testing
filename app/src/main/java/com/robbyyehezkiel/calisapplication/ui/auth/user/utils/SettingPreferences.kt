package com.robbyyehezkiel.calisapplication.ui.auth.user.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.robbyyehezkiel.calisapplication.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "users")

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val userId = stringPreferencesKey("user_id")
    private val userName = stringPreferencesKey("user_Name")
    private val userAge = intPreferencesKey("user_Age")
    private val userEmail = stringPreferencesKey("user_Email")

    fun getUserSettingsFlow(): Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                userId = preferences[userId] ?: "",
                name = preferences[userName] ?: "",
                age = preferences[userAge] ?: 0,
                email = preferences[userEmail] ?: ""
            )
        }
    }

    suspend fun saveUserSettings(userSettings: User) {
        dataStore.edit { preferences ->
            preferences[userId] = userSettings.userId
            preferences[userName] = userSettings.name
            preferences[userAge] = userSettings.age
            preferences[userEmail] = userSettings.email
        }
    }

    suspend fun clearUserSettings() {
        dataStore.edit { preferences ->
            preferences.remove(userId)
            preferences.remove(userName)
            preferences.remove(userAge)
            preferences.remove(userEmail)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
