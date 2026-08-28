package com.software.biliapp.data.remote.model

import com.software.biliapp.domain.model.QrCodeDataDomain
import kotlinx.serialization.Serializable

@Serializable
data class QrCodeData(
    val url: String,
    val qrcode_key: String
)


fun QrCodeData.toDomain(): QrCodeDataDomain {
    return QrCodeDataDomain(
        url = url,
        qrcode_key = qrcode_key
    )
}