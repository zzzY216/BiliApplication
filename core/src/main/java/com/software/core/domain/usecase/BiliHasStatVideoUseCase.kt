package com.software.biliapp.domain.usecase

import com.software.core.network.repository.BiliHasLikeVideoRepository
import com.software.biliapp.domain.model.HasLikeVideoDataDomain
import javax.inject.Inject

class BiliHasStatVideoUseCase @Inject constructor(
    private val biliHasLikeVideoRepository: BiliHasLikeVideoRepository
) {
    suspend operator fun invoke(
        aid: String? = null,
        bvid: String? = null
    ): Result<HasLikeVideoDataDomain> {
        return biliHasLikeVideoRepository.hasLikeVideo(aid = aid, bvid = bvid)
    }
}