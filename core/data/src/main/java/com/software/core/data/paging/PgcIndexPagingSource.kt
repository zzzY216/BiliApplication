package com.software.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.software.core.model.pgc.PgcIndexItem
import com.software.core.network.PgcApiService

/**
 * 番剧索引分页源（/pgc/season/index/result，按 page 递增）。
 * seasonType: 1 番剧 / 4 国创；由调用方在构造时传入。
 */
class PgcIndexPagingSource(
    private val apiService: PgcApiService,
    private val seasonType: Int,
) : PagingSource<Int, PgcIndexItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PgcIndexItem> {
        return try {
            val page = params.key ?: 1
            val response = apiService.getIndexResult(
                page = page,
                pageSize = params.loadSize.coerceIn(1, 50),
                seasonType = seasonType,
            )
            if (response.code != 0) {
                return LoadResult.Error(
                    Exception("code=${response.code}, message=${response.message}")
                )
            }
            val list = response.data?.list.orEmpty()
            LoadResult.Page(
                data = list,
                prevKey = if (page > 1) page - 1 else null,
                nextKey = if (list.isNotEmpty()) page + 1 else null,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PgcIndexItem>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}