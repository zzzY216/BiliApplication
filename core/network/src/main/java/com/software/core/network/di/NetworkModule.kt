package com.software.core.network.di

import com.software.core.network.BiliApiNetwork
import com.software.core.network.BiliApiService
import com.software.core.network.BiliAppApiService
import com.software.core.network.BiliAppNetwork
import com.software.core.network.BiliLoginApiService
import com.software.core.network.BiliLoginNetwork
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

/**
 * 网络层 DI：Json、三个 baseUrl 的 Retrofit 与对应 ApiService。
 * OkHttpClient（含 UA/Referer/Cookie 拦截器）由 :core:data 提供，
 * 因为 Cookie 拦截器需要依赖 BiliSessionManager（数据层）。
 */
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

    @BiliAppNetwork
    @Provides
    @Singleton
    fun provideBiliAppRetrofit(
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
    fun provideBiliApiRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.bilibili.com")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    /**
     * passport.bilibili.com：扫码登录链路。
     * 复用同一个 OkHttpClient，保证 UA/Referer/Cookie/日志 与主链路一致。
     */
    @BiliLoginNetwork
    @Provides
    @Singleton
    fun provideBiliLoginRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://passport.bilibili.com")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @BiliAppNetwork
    @Provides
    @Singleton
    fun provideBiliAppApiService(@BiliAppNetwork retrofit: Retrofit): BiliAppApiService {
        return retrofit.create(BiliAppApiService::class.java)
    }

    @BiliApiNetwork
    @Provides
    @Singleton
    fun provideBiliApiService(@BiliApiNetwork retrofit: Retrofit): BiliApiService {
        return retrofit.create(BiliApiService::class.java)
    }

    @BiliLoginNetwork
    @Provides
    @Singleton
    fun provideBiliLoginApiService(@BiliLoginNetwork retrofit: Retrofit): BiliLoginApiService {
        return retrofit.create(BiliLoginApiService::class.java)
    }
}
