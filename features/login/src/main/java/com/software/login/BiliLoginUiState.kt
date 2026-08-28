package com.software.login

import android.graphics.Bitmap
import com.software.biliapp.domain.model.QrCodeDataDomain
import com.software.biliapp.domain.model.QrPollDataDomain

data class BiliLoginUiState(
    val qrCodeData: QrCodeDataDomain? = null,
    val qrPollData: QrPollDataDomain? = null,

    val qrBitmap: Bitmap? = null,

    val currentUsername: String = "",
    val currentPassword: String = "",
    val currentPasswordError: String? = null,
    val currentConfirmPassword: String = "",
    val currentConfirmPasswordError: String? = null,
)