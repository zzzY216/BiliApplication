package com.software.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.software.core.model.RecommendItem
import com.software.core.network.BiliAppApiService
import com.software.core.network.BiliAppNetwork
import javax.inject.Inject

class BiliRecommendPagingSource @Inject constructor(
    @BiliAppNetwork private val apiService: BiliAppApiService
) : PagingSource<Long, RecommendItem>() {
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, RecommendItem> {
        return try {
            val currentIdx = params.key ?: 0L
            val isFirstPage = currentIdx == 0L
            val response = apiService.getRecommendList(
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
                data = items,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, RecommendItem>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
