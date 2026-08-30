package com.software.core.data.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicReference

/**
 * WBI 签名（移植自 PiliPlus lib/utils/wbi_sign.dart，对应
 * bilibili-API-collect docs/misc/sign/wbi）。
 *
 * 密钥来自 /x/web-interface/nav 的 wbi_img（img_url + sub_url 的文件名），
 * 与 PiliPlus 一致按天缓存。
 */
object WbiSign {

    /** 打乱表（取前 32 项，即 PiliPlus 的 _mixinKeyEncTab） */
    private val mixinKeyEncTab = intArrayOf(
        46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35,
        27, 43, 5, 49, 33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13,
    )

    private const val DAY_MILLIS = 86_400_000L
    private const val NAV_URL = "https://api.bilibili.com/x/web-interface/nav"

    /** 缓存：(当天自然日, mixinKey) */
    private val cache = AtomicReference<Pair<Long, String?>>(0L to null)

    /**
     * 对请求参数做 WBI 签名，返回包含原始参数 + wts + w_rid 的完整参数表。
     * [params] 中 value 为 null 的项会被忽略。
     */
    suspend fun sign(
        client: OkHttpClient,
        params: Map<String, Any?>,
    ): Map<String, String> {
        val mixinKey = getMixinKey(client)
        val result = LinkedHashMap<String, String>()
        params.forEach { (k, v) -> if (v != null) result[k] = v.toString() }
        result["wts"] = (System.currentTimeMillis() / 1000).toString()

        // value 中不允许出现 !'()* 字符，过滤后按 key 排序拼接
        val query = result.toSortedMap().entries.joinToString("&") { (k, v) ->
            encodeComponent(k) + "=" + encodeComponent(v.filter { it !in "!'()*" })
        }
        result["w_rid"] = md5(query + mixinKey)
        return result
    }

    private suspend fun getMixinKey(client: OkHttpClient): String {
        val today = System.currentTimeMillis() / DAY_MILLIS
        val (cachedDay, cachedKey) = cache.get()
        if (cachedDay == today && cachedKey != null) return cachedKey

        val key = fetchWbiKeys(client)
        if (key.isNotEmpty()) cache.set(today to key)
        return key
    }

    private suspend fun fetchWbiKeys(client: OkHttpClient): String = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(NAV_URL).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext ""
                val json = JSONObject(response.body?.string().orEmpty())
                val wbiImg = json.optJSONObject("data")?.optJSONObject("wbi_img")
                    ?: return@withContext ""
                val imgKey = fileNameWithoutExt(wbiImg.optString("img_url"))
                val subKey = fileNameWithoutExt(wbiImg.optString("sub_url"))
                getMixinKeyFromKeys(imgKey + subKey)
            }
        } catch (_: Exception) {
            ""
        }
    }

    /** 对 imgKey 和 subKey 拼接串按打乱表重排，取前 32 字符 */
    private fun getMixinKeyFromKeys(orig: String): String =
        buildString {
            for (i in mixinKeyEncTab) {
                if (i < orig.length) append(orig[i])
            }
        }

    /** 取 URL 文件名（不含扩展名），如 ".../bfs/wbi/2ef36...d33c.png" -> "2ef36...d33c" */
    private fun fileNameWithoutExt(url: String): String =
        url.substringAfterLast('/').substringBeforeLast('.')

    private fun encodeComponent(s: String): String =
        URLEncoder.encode(s, Charsets.UTF_8).replace("+", "%20")

    private fun md5(input: String): String {
        val digest = MessageDigest.getInstance("MD5").digest(input.toByteArray(Charsets.UTF_8))
        val sb = StringBuilder(digest.size * 2)
        for (b in digest) {
            val v = b.toInt() and 0xFF
            sb.append(HEX_CHARS[v ushr 4])
            sb.append(HEX_CHARS[v and 0x0F])
        }
        return sb.toString()
    }

    private const val HEX_CHARS = "0123456789abcdef"
}