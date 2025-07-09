package com.example.komiconnect.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

class DataRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val THEME_KEY = stringPreferencesKey("theme")
    }

    val token = dataStore.data.map { it[TOKEN_KEY] ?: "" }
    val theme = dataStore.data.map { it[THEME_KEY] ?: "Sistema" }

    suspend fun setToken(token: String) = dataStore.edit { it[TOKEN_KEY] = token }
    suspend fun setTheme(theme: String) = dataStore.edit { it[THEME_KEY] = theme }
    suspend fun resetToken() = dataStore.edit { it[TOKEN_KEY] = "" }
}