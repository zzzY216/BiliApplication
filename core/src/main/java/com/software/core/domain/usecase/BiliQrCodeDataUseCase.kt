package com.software.biliapp.domain.usecase

import com.software.core.network.repository.BlBlQrCodeDataRepository
import com.software.biliapp.domain.model.QrCodeDataDomain
import javax.inject.Inject

class BiliQrCodeDataUseCase @Inject constructor(
    private val blBlQrCodeDataRepository: BlBlQrCodeDataRepository
) {
    suspend operator fun invoke(): Result<QrCodeDataDomain> {
        return blBlQrCodeDataRepository.getQrCodeData()
    }
}