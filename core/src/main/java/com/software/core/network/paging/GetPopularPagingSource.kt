package com.software.core.network.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.software.biliapp.data.remote.model.toDomain
import com.software.core.network.model.toDomain
import com.software.biliapp.domain.model.PopularItemDomain
import com.software.core.network.BiliApiService
import javax.inject.Inject

class GetPopularPagingSource @Inject constructor(
    private val apiService: BiliApiService
) : PagingSource<Long, PopularItemDomain>() {
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, PopularItemDomain> {
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
                data = items.map { it.toDomain() },
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            Log.d("GetPopularPagingSource", "Error: ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, PopularItemDomain>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}