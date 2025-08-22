package com.appcenter.uniclub.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class TokenStore(private val context: Context) {
    private val KEY_AUTH_HEADER = stringPreferencesKey("auth_header") // "Bearer xxx"

    val authHeaderFlow: Flow<String?> = context.dataStore.data.map { it[KEY_AUTH_HEADER] }

    suspend fun saveAuthHeader(header: String) {
        context.dataStore.edit { it[KEY_AUTH_HEADER] = header }
    }

    suspend fun clear() {
        context.dataStore.edit { it.remove(KEY_AUTH_HEADER) }
    }

    suspend fun getAuthHeader(): String? {
        return context.dataStore.data.map { it[KEY_AUTH_HEADER] }.firstOrNull()
    }
}
