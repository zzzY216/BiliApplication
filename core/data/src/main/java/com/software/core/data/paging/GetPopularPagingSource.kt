package com.software.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.software.core.model.PopularItem
import com.software.core.network.BiliApiNetwork
import com.software.core.network.BiliApiService
import javax.inject.Inject

class GetPopularPagingSource @Inject constructor(
    @BiliApiNetwork private val apiService: BiliApiService
) : PagingSource<Long, PopularItem>() {
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, PopularItem> {
        return try {
            val currentIndex = params.key ?: 0L
            val isFirstPage = currentIndex == 0L
            val response = apiService.getPopularList(
                idx = currentIndex,
                pull = isFirstPage,
                loginEvent = 1,
                flush = if (isFirstPage) 1 else 0
            )
            val items = response.data?.list ?: emptyList()
            val prevKey = if (isFirstPage) {
                null
            } else {
                currentIndex - 1
            }
            val nextKey = if (items.isEmpty() || ((response.data?.noMore ?: true) == true)) {
                null
            } else {
                currentIndex + 1
            }
            LoadResult.Page(
                data = items,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, PopularItem>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
