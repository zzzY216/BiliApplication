package com.software.core.network.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.software.biliapp.data.remote.model.toDomain
import com.software.core.network.model.toDomain
import com.software.core.network.paging.GetPopularPagingSource
import com.software.biliapp.domain.model.PopularDataDomain
import com.software.biliapp.domain.model.PopularItemDomain
import com.software.core.network.BiliApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface BiliGetPopularListRepository {
    suspend fun getPopularList(
        idx: Long,
        pull: Boolean,
        loginEvent: Int,
        flush: Int
    ): Result<PopularDataDomain>
    fun getPopularListPagingFlow(): Flow<PagingData<PopularItemDomain>>
}

class BiliGetPopularListRepositoryImpl @Inject constructor(
    private val apiService: BiliApiService
): BiliGetPopularListRepository {
    override suspend fun getPopularList(
        idx: Long,
        pull: Boolean,
        loginEvent: Int,
        flush: Int
    ): Result<PopularDataDomain> {
        return try {
            val response = apiService.getPopularList(
                idx = idx,
                pull = pull,
                loginEvent = loginEvent,
                flush = flush
            )
            if (response.code == 0) {
                if (response.data != null) {
                    Result.success(response.data.toDomain())
                } else {
                    Result.failure(Exception("Data is null"))
                }
            } else {
                Result.failure(Exception("Code is not 0"))
            }
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPopularListPagingFlow(): Flow<PagingData<PopularItemDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                prefetchDistance = 3
            ),
            initialKey = 0,
            pagingSourceFactory = {
                GetPopularPagingSource(apiService)
            }
        ).flow
    }
}