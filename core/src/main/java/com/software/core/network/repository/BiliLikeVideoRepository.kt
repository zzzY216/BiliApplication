package com.software.core.network.repository

import android.util.Log
import com.software.core.mongo.bili.BiliSessionManager
import com.software.biliapp.domain.model.LikeVideoDataDomain
import com.software.core.network.BiliApiService
import com.software.core.network.model.toDomain
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface BiliLikeVideoRepository {
    suspend fun likeVideo(
        aid: String? = null,
        bvid: String? = null,
        like: Int,
    ): Result<LikeVideoDataDomain>
}

class BiliLikeVideoRepositoryImpl @Inject constructor(
    private val apiService: BiliApiService,
    private val biliSessionManager: BiliSessionManager
) : BiliLikeVideoRepository {
    override suspend fun likeVideo(
        aid: String?,
        bvid: String?,
        like: Int,
    ): Result<LikeVideoDataDomain> {
        return try {

            val csrf = biliSessionManager.jctFlow.first()
            Log.d("BiliLikeVideoRepositoryImpl", "likeVideo")
            val response = apiService.likeVideo(
                null, bvid, like, csrf, "https://www.bilibili.com/video/${
                    bvid
                }"
            )
            if (response.code == 0) {
                return Result.success(response.toDomain())
            } else {
                Result.failure(Exception("response.code != 0"))
            }
        } catch (e: Exception) {
            Log.e("BiliLikeVideoRepositoryImpl", e.message.toString())
            Result.failure(e)
        }
    }
}