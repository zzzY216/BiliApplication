package com.software.core.network.repository

import com.software.biliapp.domain.model.HasLikeVideoDataDomain
import com.software.core.network.BiliApiService
import com.software.core.network.model.toDomain
import javax.inject.Inject

interface BiliHasLikeVideoRepository {
    suspend fun hasLikeVideo(
        aid: String? = null,
        bvid: String? = null,
    ): Result<HasLikeVideoDataDomain>
}

class BiliHasLikeVideoRepositoryImpl @Inject constructor(
    private val apiService: BiliApiService
) : BiliHasLikeVideoRepository {
    override suspend fun hasLikeVideo(
        aid: String?,
        bvid: String?,
    ): Result<HasLikeVideoDataDomain> {
        return try {
            val response = apiService.hasLikeVideo(
                aid = aid,
                bvid = bvid,
            )
            if (response.code == 0) {
                Result.success(response.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}