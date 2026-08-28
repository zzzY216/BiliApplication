package com.software.core.network.repository

import com.software.biliapp.data.remote.model.toDomain
import com.software.core.network.model.toDomain
import com.software.biliapp.domain.model.VideoDetailDomain
import com.software.core.network.BiliApiService
import javax.inject.Inject

interface BiliGetVideoDetailRepository {
    suspend fun getVideoDetail(
        aid: String? = null,
        bvid: String? = null
    ): Result<VideoDetailDomain>
}

class BiliGetVideoDetailRepositoryImpl @Inject constructor(
    private val apiService: BiliApiService
): BiliGetVideoDetailRepository {
    override suspend fun getVideoDetail(
        aid: String?,
        bvid: String?
    ): Result<VideoDetailDomain> {
        return try {
            val response = apiService.getVideoDetail(aid, bvid)
            if (response.code == 0) {
                if (response.data != null) {
                    Result.success(response.data.toDomain())
                } else {
                    Result.failure(Exception("Data is null"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code} - ${response.message}"))
            }
        }catch (e: Exception) {
            Result.failure(e)
        }
    }
}