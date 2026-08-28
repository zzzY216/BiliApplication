package com.software.core.network.repository

import android.util.Log
import com.software.biliapp.data.remote.model.toDomain
import com.software.core.network.model.toDomain
import com.software.biliapp.domain.model.QrCodeDataDomain
import com.software.core.network.BiliLoginApiService

interface BlBlQrCodeDataRepository {
    suspend fun getQrCodeData(): Result<QrCodeDataDomain>
}

class BlBlQrCodeDataRepositoryImpl(
    private val apiService: BiliLoginApiService
) : BlBlQrCodeDataRepository {
    override suspend fun getQrCodeData(): Result<QrCodeDataDomain> {
        return try {
            val response = apiService.getQrCodeInfo()
            if (response.code == 0) {
                if (response.data != null) {
                    Result.success(response.data.toDomain())
                } else {
                    Result.failure(Exception("Data is null"))
                }
            } else {
                Result.failure(Exception("Error: ${response.message}"))
            }
        } catch (e: Exception) {
            Log.e("QRCode", "ErrorQRCodeRepository: ${e.message}")
            Result.failure(Exception(e))
        }
    }
}