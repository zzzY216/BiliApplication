package com.software.core.data.di

import com.software.core.data.session.BiliSessionManager
import com.software.core.network.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

/**
 * 应用级 OkHttpClient。
 * 放在数据层：Cookie 拦截器需要 BiliSessionManager（会话属于数据层）。
 * debug 全量打印日志，release 只打 BASIC，避免泄露 Cookie/Token。
 */
@Module
@InstallIn(SingletonComponent::class)
object OkHttpClientModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(
        biliSessionManager: BiliSessionManager
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.BASIC
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                // 直接读内存镜像，避免每次请求都 runBlocking 阻塞读 DataStore
                val currentCookie = biliSessionManager.cachedCookie
                val request = chain.request().newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    // 建议 Referer 加上结尾斜杠
                    .header("Referer", "https://www.bilibili.com/")
                    .header("Origin", "https://www.bilibili.com")
                    // 明确告诉服务器你接受 JSON
                    .header("Accept", "application/json, text/plain, */*")
                    .apply {
                        if (currentCookie.isNotEmpty()) {
                            header("Cookie", currentCookie)
                        }
                    }
                    .build()
                chain.proceed(request)
            }
            .build()
    }
}
