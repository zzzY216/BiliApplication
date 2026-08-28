package com.software.core.di

import com.software.core.mongo.bili.BiliSessionManager
import com.software.core.network.BiliApiNetwork
import com.software.core.network.BiliApiService
import com.software.core.network.BiliAppNetwork
import com.software.core.network.BiliLoginApiService
import com.software.core.network.BiliLoginNetwork
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true // 重点：忽略 JSON 中多余的字段，防止崩溃
            coerceInputValues = true // 重点：如果类型不匹配（如 null 赋给非空），尝试兼容
            isLenient = true
        }
    }


    /**
     * 基础 OkHttpClient
     * 作用：添加通用的 Referer 和 User-Agent，防止 B 站返回 403
     */
    @Provides
    @Singleton
    fun provideBaseOkhttpClient(
        biliSessionManager: BiliSessionManager
    ): OkHttpClient {
        val loggingIntercepter = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingIntercepter)
            .addInterceptor { chain ->
                val currentCookie = runBlocking {
                    biliSessionManager.cookieFlow.first()
                }
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

    @BiliAppNetwork
    @Provides
    @Singleton
    fun provideBiliNetwork(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://app.bilibili.com")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }


    @BiliApiNetwork
    @Provides
    @Singleton
    fun provideBiliPlayNetwork(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.bilibili.com")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @BiliAppNetwork
    @Provides
    @Singleton
    fun provideBiliApiService(@BiliAppNetwork retrofit: Retrofit): BiliApiService {
        return retrofit.create(BiliApiService::class.java)
    }


    @BiliLoginNetwork
    @Provides
    @Singleton
    fun provideBLBLNetwork(
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://passport.bilibili.com")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @BiliLoginNetwork
    @Provides
    @Singleton
    fun provideBLBLApiService(@BiliLoginNetwork retrofit: Retrofit): BiliLoginApiService {
        return retrofit.create(BiliLoginApiService::class.java)
    }

    @BiliApiNetwork
    @Provides
    @Singleton
    fun provideBiliPlayApiService(@BiliApiNetwork retrofit: Retrofit): BiliApiService {
        return retrofit.create(BiliApiService::class.java)
    }
}