package com.software.biliapp.domain.usecase

import com.software.core.network.repository.BiliGetVideoPlayUrlRepository
import com.software.biliapp.domain.model.PlayUrlDataDomain
import javax.inject.Inject

class BiliGetVideoPlayUrlUseCase @Inject constructor(
    private val biliGetVideoPlayUrlRepository: BiliGetVideoPlayUrlRepository
) {
    suspend operator fun invoke(
        avid: String,
        cid: String,
        qn: Int,
        type: String,
        platform: String
    ): Result<PlayUrlDataDomain> {
        return biliGetVideoPlayUrlRepository.getVideoPlayUrl(avid, cid, qn, type, platform)
    }
}