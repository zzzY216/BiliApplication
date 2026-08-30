package com.software.home.anime

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.software.core.model.pgc.PgcIndexItem
import com.software.core.model.pgc.PgcRankItem
import com.software.core.model.pgc.TimelineEpisode
import com.software.designsystem.BiliColors

/**
 * 通用番剧卡片：横向布局（封面 3:4 + 标题/角标/副标题），索引/排行/时间线共用。
 */
@Composable
fun SeasonCard(
    cover: String?,
    title: String,
    badge: String?,
    subtitle: String?,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        AsyncImage(
            model = cover,
            contentDescription = title,
            modifier = Modifier
                .width(96.dp)
                .height(128.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            badge?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = BiliColors.AccentRed,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SeasonCard(
    item: PgcIndexItem,
    onClick: () -> Unit = {},
) = SeasonCard(
    cover = item.cover,
    title = item.title.orEmpty(),
    badge = item.badge,
    subtitle = item.indexShow,
    onClick = onClick,
)

@Composable
fun SeasonCard(
    item: PgcRankItem,
    onClick: () -> Unit = {},
) = SeasonCard(
    cover = item.cover,
    title = item.title.orEmpty(),
    badge = item.newEp?.desc,
    subtitle = item.stat?.follow?.let { "追番 ${formatShortCount(it)}" },
    onClick = onClick,
)

@Composable
fun SeasonCard(
    item: TimelineEpisode,
    onClick: () -> Unit = {},
) = SeasonCard(
    cover = item.cover,
    title = item.title.orEmpty(),
    badge = item.pubIndex,
    subtitle = item.pubTime,
    onClick = onClick,
)

/** 从 bangumi 详情链接解析 season id，如 ".../bangumi/play/ss12345" -> 12345 */
fun seasonIdFromBangumiUrl(url: String?): Long? =
    url?.let { Regex("ss(\\d+)").find(it)?.groupValues?.get(1)?.toLongOrNull() }

/** 数字缩写：12345 -> "1.2万"、123456789 -> "1.2亿" */
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