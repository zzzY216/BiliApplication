package com.software.core.di

import com.software.biliapp.data.repository.BiliGetUserInfoRepository
import com.software.biliapp.domain.usecase.BiliGetReplyListUseCase
import com.software.biliapp.domain.usecase.BiliGetUserInfoUseCase
import com.software.biliapp.domain.usecase.BiliGetVideoDetailUseCase
import com.software.biliapp.domain.usecase.BiliGetVideoPlayUrlUseCase
import com.software.biliapp.domain.usecase.BiliHasStatVideoUseCase
import com.software.biliapp.domain.usecase.BiliLikeVideoUseCase
import com.software.biliapp.domain.usecase.BiliPollQrCodeStatusUseCase
import com.software.biliapp.domain.usecase.BiliQrCodeDataUseCase
import com.software.biliapp.domain.usecase.BiliRecommendVideoUseCase
import com.software.biliapp.domain.usecase.GetRecommendVideosUseCase
import com.software.core.domain.usecase.BiliGetPopularListUseCase
import com.software.core.network.repository.BiliGetPopularListRepository
import com.software.core.network.repository.BiliGetReplyListRepository
import com.software.core.network.repository.BiliGetVideoDetailRepository
import com.software.core.network.repository.BiliGetVideoPlayUrlRepository
import com.software.core.network.repository.BiliHasLikeVideoRepository
import com.software.core.network.repository.BiliLikeVideoRepository
import com.software.core.network.repository.BiliRecommendVideoRepository
import com.software.core.network.repository.BlBlPollQrCodeStatusRepository
import com.software.core.network.repository.BlBlQrCodeDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object BiliUseCaseModule {
    @Provides
    fun provideBiliHasStatUseCase(
        biliHasLikeVideoRepository: BiliHasLikeVideoRepository
    ): BiliHasStatVideoUseCase {
        return BiliHasStatVideoUseCase(biliHasLikeVideoRepository)
    }

    @Provides
    fun provideBiliLikeVideoUseCase(
        biliLikeVideoRepository: BiliLikeVideoRepository
    ): BiliLikeVideoUseCase {
        return BiliLikeVideoUseCase(biliLikeVideoRepository)
    }

    @Provides
    fun provideBiliGetVideoPlayUrlUseCase(
        biliGetVideoPlayUrlRepository: BiliGetVideoPlayUrlRepository
    ): BiliGetVideoPlayUrlUseCase {
        return BiliGetVideoPlayUrlUseCase(biliGetVideoPlayUrlRepository)
    }

    @Provides
    fun provideBiliQrCodeDataUseCase(
        biliQrCodeRepository: BlBlQrCodeDataRepository
    ): BiliQrCodeDataUseCase {
        return BiliQrCodeDataUseCase(biliQrCodeRepository)
    }

    @Provides
    fun provideBiliPollQrCodeStatusUseCase(
        biliPollQrCodeStatusRepository: BlBlPollQrCodeStatusRepository
    ): BiliPollQrCodeStatusUseCase {
        return BiliPollQrCodeStatusUseCase(biliPollQrCodeStatusRepository)
    }

    @Provides
    fun provideBiliRecommendVideoUseCase(
        biliRecommendVideoRepository: BiliRecommendVideoRepository
    ): BiliRecommendVideoUseCase {
        return BiliRecommendVideoUseCase(biliRecommendVideoRepository)
    }

    @Provides
    fun provideBiliRecommendVideoPagingUseCase(
        biliRecommendVideoRepository: BiliRecommendVideoRepository
    ): GetRecommendVideosUseCase {
        return GetRecommendVideosUseCase(biliRecommendVideoRepository)
    }

    @Provides
    fun provideBiliGetVideoDetailUseCase(
        biliGetVideoDetailRepository: BiliGetVideoDetailRepository
    ): BiliGetVideoDetailUseCase {
        return BiliGetVideoDetailUseCase(biliGetVideoDetailRepository)
    }

    @Provides
    fun provideBiliGetUserInfoUseCase(
        biliGetUserInfoRepository: BiliGetUserInfoRepository
    ): BiliGetUserInfoUseCase {
        return BiliGetUserInfoUseCase(biliGetUserInfoRepository)
    }

    @Provides
    fun provideBiliGetReplyListUseCase(
        biliGetReplyListRepository: BiliGetReplyListRepository
    ): BiliGetReplyListUseCase {
        return BiliGetReplyListUseCase(biliGetReplyListRepository)
    }

    @Provides
    fun provideBiliGetPopularListUseCase(
        biliGetPopularListRepository: BiliGetPopularListRepository
    ): BiliGetPopularListUseCase {
        return BiliGetPopularListUseCase(biliGetPopularListRepository)
    }
}