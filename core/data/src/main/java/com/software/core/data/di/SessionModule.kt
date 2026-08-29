package com.software.core.data.di

import android.content.Context
import com.software.core.data.session.BiliSessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SessionModule {
    @Provides
    @Singleton
    fun provideBiliSessionManager(
        @ApplicationContext context: Context
    ): BiliSessionManager {
        return BiliSessionManager(context)
    }
}
