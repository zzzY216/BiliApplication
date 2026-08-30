package com.software.core.data.di

import com.software.core.data.repository.AuthRepository
import com.software.core.data.repository.AuthRepositoryImpl
import com.software.core.data.repository.BangumiRepository
import com.software.core.data.repository.BangumiRepositoryImpl
import com.software.core.data.repository.UserRepository
import com.software.core.data.repository.UserRepositoryImpl
import com.software.core.data.repository.VideoRepository
import com.software.core.data.repository.VideoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 数据层绑定：实现类全部是 @Inject 构造，接口用 @Binds 绑定即可。
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindVideoRepository(impl: VideoRepositoryImpl): VideoRepository

    @Binds
    @Singleton
    abstract fun bindBangumiRepository(impl: BangumiRepositoryImpl): BangumiRepository
}
