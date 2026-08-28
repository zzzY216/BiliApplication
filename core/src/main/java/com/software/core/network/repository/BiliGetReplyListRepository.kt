package com.software.core.network.repository

import android.util.Log
import com.software.core.network.model.toDomain
import com.software.biliapp.domain.model.ReplyDataDomain
import com.software.core.network.BiliApiService
import jakarta.inject.Inject

interface BiliGetReplyListRepository {
    suspend fun getReplyList(
        oid: Long,
        type: Int,
        sort: Int,
        pn: Int,
        ps: Int
    ): Result<ReplyDataDomain>
}

class BiliGetReplyListRepositoryImpl @Inject constructor(
    private val apiService: BiliApiService
) : BiliGetReplyListRepository {
    override suspend fun getReplyList(
        oid: Long,
        type: Int,
        sort: Int,
        pn: Int,
        ps: Int
    ): Result<ReplyDataDomain> {
        return try {
            val response = apiService.getReplyList(oid, type, sort, pn, ps)
            if (response.code == 0) {
                if (response.data != null) {
                    Log.d("BiliGetReplyListRepository", "Data is right")
                    Result.success(response.data.toDomain())
                } else {
                    Log.e("BiliGetReplyListRepository", "Data is null")
                    Result.failure(Exception("Data is null"))
                }
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}