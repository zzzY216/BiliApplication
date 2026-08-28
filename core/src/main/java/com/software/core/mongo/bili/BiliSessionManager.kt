package com.software.core.mongo.bili

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.text.get

class BiliSessionManager(
    private val context: Context
) {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "bili_settings")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val SESSDATA = stringPreferencesKey("sessdata")
        private val BILI_JCT = stringPreferencesKey("bili_jct")
        private val DEDE_USER_ID = stringPreferencesKey("dede_user_id")
    }

    suspend fun saveLoginSession(url: String, refreshToken: String) {
        val uri = Uri.parse(url)
        val sessData = uri.getQueryParameter("SESSDATA") ?: ""
        val biliJct = uri.getQueryParameter("bili_jct") ?: ""
        val userId = uri.getQueryParameter("DedeUserID") ?: ""
        context.dataStore.edit { preferences ->
            preferences[SESSDATA] = sessData
            preferences[BILI_JCT] = biliJct
            preferences[DEDE_USER_ID] = userId
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    val jctFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[BILI_JCT] ?: ""
    }

    val cookieFlow: Flow<String> = context.dataStore.data.map { preferences ->
        val sd = preferences[SESSDATA] ?: ""
        val bjct = preferences[BILI_JCT] ?: ""
        val uid = preferences[DEDE_USER_ID] ?: ""
        if (sd.isNotEmpty()) {
            "SESSDATA=$sd; bili_jct=$bjct; DedeUserID=$uid;"
        } else {
            ""
        }
    }
}