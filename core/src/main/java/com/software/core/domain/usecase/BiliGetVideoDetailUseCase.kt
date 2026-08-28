package com.software.biliapp.domain.usecase

import com.software.core.network.repository.BiliGetVideoDetailRepository
import com.software.biliapp.domain.model.VideoDetailDomain
import javax.inject.Inject

class BiliGetVideoDetailUseCase @Inject constructor(
    private val biliGetVideoDetailRepository: BiliGetVideoDetailRepository
) {
    suspend operator fun invoke(
        aid: String? = null,
        bvid: String? = null
    ): Result<VideoDetailDomain> {
        return biliGetVideoDetailRepository.getVideoDetail(aid, bvid)
    }
}