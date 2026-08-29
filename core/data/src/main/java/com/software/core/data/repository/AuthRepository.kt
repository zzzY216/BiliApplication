package com.software.core.data.repository

import com.software.core.data.biliApiCall
import com.software.core.data.session.BiliSessionManager
import com.software.core.model.QrCodeData
import com.software.core.model.QrPollData
import com.software.core.network.BiliLoginApiService
import com.software.core.network.BiliLoginNetwork
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 认证域仓储：扫码登录链路 + 会话管理。
 */
interface AuthRepository {
    suspend fun getQrCode(): Result<QrCodeData>

    suspend fun pollQrCodeStatus(qrcodeKey: String): Result<QrPollData>

    suspend fun saveSession(url: String, refreshToken: String)

    suspend fun clearSession()

    fun cookieFlow(): Flow<String>
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @BiliLoginNetwork private val loginApiService: BiliLoginApiService,
    private val sessionManager: BiliSessionManager,
) : AuthRepository {

    override suspend fun getQrCode(): Result<QrCodeData> =
        biliApiCall { loginApiService.getQrCodeInfo() }

    override suspend fun pollQrCodeStatus(qrcodeKey: String): Result<QrPollData> {
        return try {
            Result.success(loginApiService.pollQrCodeStatus(qrcodeKey))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveSession(url: String, refreshToken: String) {
        sessionManager.saveLoginSession(url, refreshToken)
    }

    override suspend fun clearSession() {
        sessionManager.clearSession()
    }

    override fun cookieFlow(): Flow<String> = sessionManager.cookieFlow
}
