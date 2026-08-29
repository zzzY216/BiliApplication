package com.software.core.data.repository

import com.software.core.data.biliApiCall
import com.software.core.model.UserInfo
import com.software.core.network.BiliApiNetwork
import com.software.core.network.BiliApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用户域仓储。
 */
interface UserRepository {
    suspend fun getUserInfo(cookie: String): Result<UserInfo>
}

@Singleton
class UserRepositoryImpl @Inject constructor(
    @BiliApiNetwork private val apiService: BiliApiService,
) : UserRepository {

    override suspend fun getUserInfo(cookie: String): Result<UserInfo> =
        biliApiCall { apiService.getUserInfo(cookie) }
}
