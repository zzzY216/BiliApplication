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
     * 注意：Kotlin 按源码顺序初始化属性/init 块。
     * 以下 Flow 必须声明在 init 的 launch 之前，否则 IO 线程可能在
     * 构造函数尚未完成时读到 null 字段（曾导致 first() NPE 崩溃）。
     */
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

    /**
     * Cookie 内存镜像：OkHttp 拦截器同步读取，避免每次请求阻塞读 DataStore。
     * 启动时异步加载一次，登录/登出时同步更新。
     */
    @Volatile
    var cachedCookie: String = ""
        private set

    init {
        scope.launch {
            // 读盘失败（IO/数据损坏）时保持空 Cookie，不让协程异常击穿进程
            runCatching { cookieFlow.first() }
                .onSuccess { cachedCookie = it }
        }
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

    private fun buildCookie(sessData: String, biliJct: String, userId: String): String =
        "SESSDATA=$sessData; bili_jct=$biliJct; DedeUserID=$userId;"
}
