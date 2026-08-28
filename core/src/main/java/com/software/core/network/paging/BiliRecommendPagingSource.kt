package com.software.core.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.software.biliapp.data.remote.model.toDomain
import com.software.core.network.model.toDomain
import com.software.biliapp.domain.model.RecommendItemDomain
import com.software.core.network.BiliApiService
import javax.inject.Inject

class BiliRecommendPagingSource @Inject constructor(
    private val apiSerVice: BiliApiService
) : PagingSource<Long, RecommendItemDomain>() {
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, RecommendItemDomain> {
        return try {
            val currentIdx = params.key ?: 0L
            val isFirstPage = currentIdx == 0L
            val response = apiSerVice.getRecommendList(
                idx = currentIdx,
                pull = isFirstPage,
                loginEvent = 1,
                flush = if (isFirstPage) 1 else 0
            )
            val items = response.data?.items ?: emptyList()
            val nextKey = if (items.isEmpty()) {
                null
            } else {
                items.last().idx
            }
            LoadResult.Page(
                data = items.map { it.toDomain() },
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, RecommendItemDomain>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}