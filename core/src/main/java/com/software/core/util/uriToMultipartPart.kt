package com.software.core.util

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun uriToMultipartPart(context: Context, uri: Uri, partName: String) {
    val contentResolver = context.contentResolver
    // 向系统询问这个资源到底是什么格式（是 JPEG、PNG 还是 GIF）。
    val type = contentResolver.getType(uri)
    // 这是一个系统查表工具，把复杂的 MIME 类型（image/jpeg）转成我们熟悉的后缀名（jpg），方便后面创建临时文件。
    val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(type)

    val tempFile = File(context.cacheDir, "temp_${System.currentTimeMillis()}.$extension")
    contentResolver.openInputStream(uri)?.use { inputStream ->
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    val requestFile = tempFile.asRequestBody(type?.toMediaTypeOrNull())
    MultipartBody.Part.createFormData(partName, tempFile.name, requestFile)
}