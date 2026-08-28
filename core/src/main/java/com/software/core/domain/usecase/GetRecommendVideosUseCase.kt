package com.software.biliapp.domain.usecase

import androidx.paging.PagingData
import com.software.core.network.repository.BiliRecommendVideoRepository
import com.software.biliapp.domain.model.RecommendItemDomain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecommendVideosUseCase @Inject constructor(
    private val repository: BiliRecommendVideoRepository
) {
    // 操作符重载，让你可以像调用函数一样使用 useCase()
    operator fun invoke(): Flow<PagingData<RecommendItemDomain>> {
        return repository.getRecommendVideoPagingFlow()
    }
}