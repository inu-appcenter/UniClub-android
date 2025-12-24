package com.appcenter.uniclub.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.fcmDataStore by preferencesDataStore(name = "fcm_token_store")

class FcmTokenStore(private val context: Context) {

    private val KEY_PENDING = stringPreferencesKey("pending_fcm_token")
    private val KEY_LAST_SENT = stringPreferencesKey("last_sent_fcm_token")

    suspend fun getPendingToken(): String? =
        context.fcmDataStore.data.map { it[KEY_PENDING] }.first()

    suspend fun setPendingToken(token: String?) {
        context.fcmDataStore.edit { prefs ->
            if (token == null) prefs.remove(KEY_PENDING) else prefs[KEY_PENDING] = token
        }
    }

    suspend fun getLastSentToken(): String? =
        context.fcmDataStore.data.map { it[KEY_LAST_SENT] }.first()

    suspend fun setLastSentToken(token: String?) {
        context.fcmDataStore.edit { prefs ->
            if (token == null) prefs.remove(KEY_LAST_SENT) else prefs[KEY_LAST_SENT] = token
        }
    }
}