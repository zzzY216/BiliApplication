package com.software.biliapp.domain.usecase

import com.software.biliapp.data.repository.BiliGetUserInfoRepository
import com.software.core.domain.model.UserInfoDomain
import javax.inject.Inject

class BiliGetUserInfoUseCase @Inject constructor(
    private val biliGetUserInfoRepository: BiliGetUserInfoRepository
) {
    suspend operator fun invoke(cookie: String): Result<UserInfoDomain> {
        return biliGetUserInfoRepository.getUserInfo(cookie)
    }
}