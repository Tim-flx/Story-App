package com.felixtp.storyappsub2.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = UserPreference.USER_PREF)

class UserPreference(private val dataStore: DataStore<Preferences>) {

    val tokenFlow: Flow<String>
        get() = dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }

    val sessionFlow: Flow<Boolean>
        get() = dataStore.data.map { preferences ->
            preferences[STATE_KEY] ?: false
        }

    suspend fun saveUser(token: String, isLogin: Boolean) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[STATE_KEY] = isLogin
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = ""
            preferences[STATE_KEY] = false
        }
    }

    companion object {
        const val USER_PREF = "user_prefs"
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val STATE_KEY = booleanPreferencesKey("state")

        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreference(dataStore).also { INSTANCE = it }
            }
        }
    }
}
