package com.software.biliapp.domain.usecase

import com.software.core.network.repository.BiliRecommendVideoRepository
import com.software.biliapp.domain.model.RecommendDataDomain
import javax.inject.Inject

class BiliRecommendVideoUseCase @Inject constructor(
    private val biliRecommendVideoRepository: BiliRecommendVideoRepository
) {
    suspend operator fun invoke(
        idx: Long,
        pull: Boolean,
        loginEvent: Int,
        flush: Int
    ): Result<RecommendDataDomain> {
        return biliRecommendVideoRepository.getRecommendVideo(idx, pull, loginEvent, flush)
    }
}