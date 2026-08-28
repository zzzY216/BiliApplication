package com.software.biliapp.domain.usecase

import com.software.core.network.repository.BiliGetReplyListRepository
import com.software.biliapp.domain.model.ReplyDataDomain
import javax.inject.Inject

class BiliGetReplyListUseCase @Inject constructor(
    private val biliGetReplyListRepository: BiliGetReplyListRepository
) {
    suspend operator fun invoke(
        oid: Long,
        type: Int,
        sort: Int,
        pn: Int,
        ps: Int
    ): Result<ReplyDataDomain> {
        return biliGetReplyListRepository.getReplyList(oid, type, sort, pn, ps)
    }
}