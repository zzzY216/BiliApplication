package com.software.home.util

/** 数字缩写：12345 -> "1.2万"、123456789 -> "1.2亿"；null -> "" */
fun formatShortCount(count: Long?): String {
    if (count == null) return ""
    return when {
        count >= 100_000_000 -> trimZero("%.1f".format(count / 100_000_000.0)) + "亿"
        count >= 10_000 -> trimZero("%.1f".format(count / 10_000.0)) + "万"
        else -> count.toString()
    }
}

private fun trimZero(s: String): String =
    s.trimEnd('0').trimEnd('.')