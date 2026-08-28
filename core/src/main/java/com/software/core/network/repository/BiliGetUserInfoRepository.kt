package com.software.biliapp.data.repository

import com.software.biliapp.data.remote.model.toDomain
import com.software.core.network.model.toDomain
import com.software.core.domain.model.UserInfoDomain
import com.software.core.network.BiliApiService
import javax.inject.Inject

interface BiliGetUserInfoRepository {
    suspend fun getUserInfo(cookie: String): Result<UserInfoDomain>
}

class BiliGetUserInfoRepositoryImpl @Inject constructor(
    private val apiService: BiliApiService
) : BiliGetUserInfoRepository {
    override suspend fun getUserInfo(cookie: String): Result<UserInfoDomain> {
        return try {
            val response = apiService.getUserInfo(cookie)
            if (response.code == 0) {
               if (response.data != null) {
                   Result.success(response.data.toDomain())
               } else {
                   Result.failure(Exception("Failed to get user info"))
               }
            } else {
                Result.failure(Exception("Failed to get user info"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}