package com.software.biliapp.domain.usecase

import com.software.core.network.repository.BiliLikeVideoRepository
import com.software.biliapp.domain.model.LikeVideoDataDomain
import javax.inject.Inject

class BiliLikeVideoUseCase @Inject constructor(
    private val biliLikeVideoRepository: BiliLikeVideoRepository
) {
    suspend operator fun invoke(
        aid: String? = null,
        bvid: String? = null,
        like: Int,
    ): Result<LikeVideoDataDomain> {
        return biliLikeVideoRepository.likeVideo(aid, bvid, like)
    }
}