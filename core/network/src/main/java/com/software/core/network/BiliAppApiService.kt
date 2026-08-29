package com.software.core.network

import com.software.core.model.BiliResponse
import com.software.core.model.RecommendData
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * app.bilibili.com 域接口（推荐流）。
 * 与 [BiliApiService]（api.bilibili.com）按 baseUrl 拆分。
 */
interface BiliAppApiService {
    /**
     * @param idx 上一次返回列表中最后一条视频的 idx
     * @param pull true 为下拉刷新(获取最新), false 为上拉加载更多(获取下一页)
     * @param loginEvent 登录事件，翻页自增或固定传 2
     */
    @GET("/x/v2/feed/index")
    suspend fun getRecommendList(
        @Query("idx") idx: Long = 0,
        @Query("pull") pull: Boolean = true, // 翻页必须传 false
        @Query("login_event") loginEvent: Int = 1,
        @Query("flush") flush: Int = 0
    ): BiliResponse<RecommendData>
}
