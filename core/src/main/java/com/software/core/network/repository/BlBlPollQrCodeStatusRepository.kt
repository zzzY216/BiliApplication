package com.software.core.network.repository

import android.util.Log
import com.software.biliapp.data.remote.model.toDomain
import com.software.core.network.model.toDomain
import com.software.biliapp.domain.model.QrPollDataDomain
import com.software.core.network.BiliLoginApiService

interface BlBlPollQrCodeStatusRepository {
    suspend fun pollQrCodeStatus(qrcodeKey: String): Result<QrPollDataDomain>
}

class BlBlPollQrCodeStatusRepositoryImpl(
    private val apiService: BiliLoginApiService
) : BlBlPollQrCodeStatusRepository {
    override suspend fun pollQrCodeStatus(qrcodeKey: String): Result<QrPollDataDomain> {
        return try {
            val response = apiService.pollQrCodeStatus(qrcodeKey)
            if (response.code == 0) {
                Result.success(response.toDomain())
            } else {
                Result.failure(Exception("Code is not 200"))
            }
        } catch (e: Exception) {
            Log.d("QRCode", "QRCodeRepository二维码状态查询失败: ${e.message}")
            Result.failure(e)
        }
    }
}