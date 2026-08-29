package com.software.core.model

import kotlinx.serialization.Serializable

@Serializable
data class QrCodeData(
    val url: String,
    val qrcode_key: String
)
