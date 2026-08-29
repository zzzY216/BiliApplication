package com.software.core.data.session

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Cookie 内存镜像：OkHttp 拦截器同步读取，避免每次请求阻塞读 DataStore。
     * 启动时异步加载一次，登录/登出时同步更新。
     */
    @Volatile
    var cachedCookie: String = ""
        private set

    init {
        scope.launch { cachedCookie = cookieFlow.first() }
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
        cachedCookie = buildCookie(sessData, biliJct, userId)
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
        cachedCookie = ""
    }

    val jctFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[BILI_JCT] ?: ""
    }

    val cookieFlow: Flow<String> = context.dataStore.data.map { preferences ->
        val sd = preferences[SESSDATA] ?: ""
        val bjct = preferences[BILI_JCT] ?: ""
        val uid = preferences[DEDE_USER_ID] ?: ""
        if (sd.isNotEmpty()) {
            buildCookie(sd, bjct, uid)
        } else {
            ""
        }
    }

    private fun buildCookie(sessData: String, biliJct: String, userId: String): String =
        "SESSDATA=$sessData; bili_jct=$biliJct; DedeUserID=$userId;"
}
