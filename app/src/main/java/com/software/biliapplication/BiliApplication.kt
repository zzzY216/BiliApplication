package com.software.biliapplication

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient

/**
 * 全局 Coil 单例 ImageLoader（Coil 3.0.4 API：SingletonImageLoader.setSafe）。
 * B站图床（*.hdslb.com）有防盗链，不带 Referer 会 403，导致首页/动漫封面不显示。
 * 这里对 hdslb.com 域名统一追加 `Referer: https://www.bilibili.com`；
 * 图片走独立 OkHttpClient，与 API 链路解耦。
 *
 * setSafe 在单例已被初始化（如某处提前触发了 ImageLoader(context)）时会抛异常，
 * 防止配置被悄悄覆盖；Coil 3 单例为懒加载，Application.onCreate 阶段调用是安全的。
 */
@HiltAndroidApp
class BiliApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SingletonImageLoader.setSafe(object : SingletonImageLoader.Factory {
            override fun newImageLoader(context: Context): ImageLoader {
                val imageOkHttpClient = OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request()
                        val newRequest = if (request.url.host.endsWith("hdslb.com")) {
                            request.newBuilder()
                                .header("Referer", "https://www.bilibili.com")
                                .build()
                        } else {
                            request
                        }
                        chain.proceed(newRequest)
                    }
                    .build()
                return ImageLoader.Builder(context)
                    .components {
                        add(OkHttpNetworkFetcherFactory(callFactory = { imageOkHttpClient }))
                    }
                    .crossfade(true)
                    .build()
            }
        })
    }
}