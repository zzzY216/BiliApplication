package com.software.core.domain.usecase

import androidx.paging.PagingData
import com.software.core.network.repository.BiliGetPopularListRepository
import com.software.biliapp.domain.model.PopularItemDomain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BiliGetPopularListUseCase @Inject constructor(
    private val biliGetPopularListRepository: BiliGetPopularListRepository
) {
    operator fun invoke() : Flow<PagingData<PopularItemDomain>> {
        return biliGetPopularListRepository.getPopularListPagingFlow()
    }
}