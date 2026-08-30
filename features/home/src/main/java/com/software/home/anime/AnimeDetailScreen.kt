package com.software.home.anime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.software.core.model.pgc.PgcEpisode
import com.software.core.model.pgc.SeasonDetail
import com.software.designsystem.BiliColors

@Composable
fun AnimeDetailRoute(
    seasonId: Long,
    viewModel: AnimeDetailViewModel = hiltViewModel(),
) {
    LaunchedEffect(seasonId) { viewModel.load(seasonId) }
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.loading -> LoadingBox(Modifier.fillMaxSize())
        uiState.error != null -> ErrorBox(
            message = uiState.error,
            onRetry = { viewModel.load(seasonId) },
            modifier = Modifier.fillMaxSize()
        )
        else -> uiState.season?.let { season ->
            AnimeDetailContent(
                season = season,
                followToggling = uiState.followToggling,
                onFollowClick = viewModel::toggleFollow,
            )
        }
    }
}

@Composable
private fun AnimeDetailContent(
    season: SeasonDetail,
    followToggling: Boolean,
    onFollowClick: () -> Unit,
) {
    val followed = season.userStatus?.follow == 1
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 头部：封面 + 标题/评分/追番
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            AsyncImage(
                model = season.cover,
                contentDescription = season.title,
                modifier = Modifier
                    .width(110.dp)
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = season.title.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                season.seasonTitle?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                season.publish?.pubTimeShow?.let {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                season.rating?.score?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "评分 ★ $it",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BiliColors.AccentRed
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onFollowClick,
                    enabled = !followToggling,
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(text = if (followed) "已追番" else "追番")
                }
            }
        }
        // 数据行
        season.stat?.let { stat ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatText(label = "播放", value = stat.views?.let(::formatShortCount))
                StatText(label = "追番", value = stat.favorite?.let(::formatShortCount))
                StatText(label = "弹幕", value = stat.danmakus?.let(::formatShortCount))
            }
        }
        // 简介
        season.evaluate?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp),
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
        }
        // 分集
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "分集",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        val episodes = season.episodes.orEmpty()
        if (episodes.isEmpty()) {
            Text(
                text = "暂无分集信息",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        } else {
            episodes.forEachIndexed { index, episode ->
                EpisodeRow(episode = episode, index = index)
            }
            // M4 TODO: 点击分集进入播放器（getPlayUrl + media3）
            Text(
                text = "播放器开发中（M4）",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StatText(label: String, value: String?) {
    Column {
        Text(
            text = value ?: "-",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EpisodeRow(episode: PgcEpisode, index: Int) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = episode.title ?: "第${index + 1}话",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            episode.longTitle?.takeIf { it != episode.title }?.let { longTitle ->
                Text(
                    text = longTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(2f)
                )
            }
            episode.duration?.let { duration ->
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatDuration(duration),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

/** 毫秒时长 -> "mm:ss" */
private fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    return "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}