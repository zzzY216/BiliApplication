package com.software.core.di

import com.software.biliapp.data.repository.BiliGetUserInfoRepository
import com.software.biliapp.data.repository.BiliGetUserInfoRepositoryImpl
import com.software.core.mongo.bili.BiliSessionManager
import com.software.core.network.BiliApiNetwork
import com.software.core.network.BiliApiService
import com.software.core.network.BiliAppNetwork
import com.software.core.network.BiliLoginApiService
import com.software.core.network.BiliLoginNetwork
import com.software.core.network.repository.BiliGetPopularListRepository
import com.software.core.network.repository.BiliGetPopularListRepositoryImpl
import com.software.core.network.repository.BiliGetReplyListRepository
import com.software.core.network.repository.BiliGetReplyListRepositoryImpl
import com.software.core.network.repository.BiliGetVideoDetailRepository
import com.software.core.network.repository.BiliGetVideoDetailRepositoryImpl
import com.software.core.network.repository.BiliGetVideoPlayUrlRepository
import com.software.core.network.repository.BiliGetVideoPlayUrlRepositoryImpl
import com.software.core.network.repository.BiliHasLikeVideoRepository
import com.software.core.network.repository.BiliHasLikeVideoRepositoryImpl
import com.software.core.network.repository.BiliLikeVideoRepository
import com.software.core.network.repository.BiliLikeVideoRepositoryImpl
import com.software.core.network.repository.BiliRecommendVideoRepository
import com.software.core.network.repository.BiliRecommendVideoRepositoryImpl
import com.software.core.network.repository.BlBlPollQrCodeStatusRepository
import com.software.core.network.repository.BlBlPollQrCodeStatusRepositoryImpl
import com.software.core.network.repository.BlBlQrCodeDataRepository
import com.software.core.network.repository.BlBlQrCodeDataRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BiliRepositoryModule {
    @Provides
    @Singleton
    fun provideBiliHasLikeVideoRepository(
        @BiliApiNetwork apiService: BiliApiService
    ): BiliHasLikeVideoRepository {
        return BiliHasLikeVideoRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideBiliLikeVideoRepository(
        @BiliApiNetwork apiService: BiliApiService,
        biliSessionManager: BiliSessionManager
    ): BiliLikeVideoRepository {
        return BiliLikeVideoRepositoryImpl(apiService, biliSessionManager)
    }

    @Provides
    @Singleton
    fun provideBiliGetVideoPlayUrlRepository(
        @BiliApiNetwork apiService: BiliApiService
    ): BiliGetVideoPlayUrlRepository {
        return BiliGetVideoPlayUrlRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideBlBlQrCodeDataRepository(
        @BiliLoginNetwork apiService: BiliLoginApiService
    ): BlBlQrCodeDataRepository {
        return BlBlQrCodeDataRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideBlBlpollQrCodeStatusRepository(
        @BiliLoginNetwork apiService: BiliLoginApiService
    ): BlBlPollQrCodeStatusRepository {
        return BlBlPollQrCodeStatusRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideBiliRecommendVideoRepository(
        @BiliAppNetwork apiService: BiliApiService
    ): BiliRecommendVideoRepository {
        return BiliRecommendVideoRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideBiliGetVideoDetailRepository(
        @BiliApiNetwork apiService: BiliApiService
    ): BiliGetVideoDetailRepository {
        return BiliGetVideoDetailRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideBiliGetUserInfoRepository(
        @BiliApiNetwork apiService: BiliApiService
    ): BiliGetUserInfoRepository {
        return BiliGetUserInfoRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideBiliGetReplyListRepository(
        @BiliApiNetwork apiService: BiliApiService
    ): BiliGetReplyListRepository {
        return BiliGetReplyListRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideBiliGetPopularListRepository(
        @BiliApiNetwork apiService: BiliApiService
    ): BiliGetPopularListRepository {
        return BiliGetPopularListRepositoryImpl(apiService)
    }
}