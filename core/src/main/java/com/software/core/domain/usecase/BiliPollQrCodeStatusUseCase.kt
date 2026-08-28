package com.software.biliapp.domain.usecase

import com.software.core.network.repository.BlBlPollQrCodeStatusRepository
import com.software.biliapp.domain.model.QrPollDataDomain
import javax.inject.Inject

class BiliPollQrCodeStatusUseCase @Inject constructor(
    private val biliPollQrCodeStatusRepository: BlBlPollQrCodeStatusRepository
) {
    suspend operator fun invoke(qrcodeKey: String): Result<QrPollDataDomain> {
        return biliPollQrCodeStatusRepository.pollQrCodeStatus(qrcodeKey)
    }
}