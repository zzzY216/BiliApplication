package com.software.core.network.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.software.biliapp.data.remote.model.toDomain
import com.software.core.network.model.toDomain
import com.software.biliapp.domain.model.RecommendDataDomain
import com.software.biliapp.domain.model.RecommendItemDomain
import com.software.core.network.BiliApiService
import com.software.core.network.paging.BiliRecommendPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface BiliRecommendVideoRepository {
    suspend fun getRecommendVideo(
        idx: Long,
        pull: Boolean,
        loginEvent: Int,
        flush: Int
    ): Result<RecommendDataDomain>

    fun getRecommendVideoPagingFlow(): Flow<PagingData<RecommendItemDomain>>
}

class BiliRecommendVideoRepositoryImpl @Inject constructor(
    private val apiService: BiliApiService
) : BiliRecommendVideoRepository {
    override suspend fun getRecommendVideo(
        idx: Long,
        pull: Boolean,
        loginEvent: Int,
        flush: Int
    ): Result<RecommendDataDomain> {
        return try {
            val response = apiService.getRecommendList(
                idx = idx,
                pull = pull,
                loginEvent = loginEvent,
                flush = flush
            )
            if (response.code == 0) {
                if (response.data != null) {
                    Log.d("BiliRecommendVideoRepository", "Data: ${response.data}")
                    Result.success(response.data.toDomain())
                } else {
                    Log.d("BiliRecommendVideoRepository", "Data: ${response.data}")
                    Result.failure(Exception("Data is null"))
                }
            } else {
                Log.d("BiliRecommendVideoRepository", "Data: ${response.data}")
                Result.failure(Exception("Code is not 0"))
            }
        } catch (e: Exception) {
            Log.e("BiliRecommendVideoRepository", "Error: ${e.message}")
            Result.failure(e)
        }
    }

    override fun getRecommendVideoPagingFlow(): Flow<PagingData<RecommendItemDomain>> {
        Log.d("API_DEBUG", "开始请求推荐列表...")
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                prefetchDistance = 3
            ),
            pagingSourceFactory = {
                BiliRecommendPagingSource(apiService)
            }
        ).flow
    }
}