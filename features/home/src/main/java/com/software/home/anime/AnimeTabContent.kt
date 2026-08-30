package com.software.home.anime

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems

private val ANIME_SUB_TABS = listOf("推荐", "排行", "时间线")

/** 动漫 Tab 内容槽：推荐（索引）/ 排行 / 时间线 三个子页签 */
@Composable
fun AnimeTabContent(
    onSeasonClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var subTab by remember { mutableIntStateOf(0) }
    Column(modifier = modifier) {
        TabRow(selectedTabIndex = subTab) {
            ANIME_SUB_TABS.forEachIndexed { index, title ->
                Tab(
                    selected = subTab == index,
                    onClick = { subTab = index },
                    text = { Text(title) }
                )
            }
        }
        when (subTab) {
            0 -> AnimeIndexList(onSeasonClick = onSeasonClick)
            1 -> AnimeRankList(onSeasonClick = onSeasonClick)
            else -> AnimeTimelineList(onSeasonClick = onSeasonClick)
        }
    }
}

/** 推荐：番剧索引（分页） */
@Composable
private fun AnimeIndexList(
    onSeasonClick: (Long) -> Unit,
    viewModel: AnimeViewModel = hiltViewModel(),
) {
    val items = viewModel.indexPagingData.collectAsLazyPagingItems()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(count = items.itemCount) { index ->
            items[index]?.let { item ->
                SeasonCard(
                    item = item,
                    onClick = { item.seasonId?.let(onSeasonClick) }
                )
            }
        }
        when {
            items.itemCount == 0 && items.loadState.refresh is LoadState.Loading -> {
                item { LoadingBox() }
            }
            items.itemCount == 0 && items.loadState.refresh is LoadState.Error -> {
                item {
                    ErrorBox(
                        message = (items.loadState.refresh as LoadState.Error).error.message,
                        onRetry = { items.retry() }
                    )
                }
            }
        }
    }
}

/** 排行：番剧排行（一次性） */
@Composable
private fun AnimeRankList(
    onSeasonClick: (Long) -> Unit,
    viewModel: AnimeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    when {
        uiState.rankLoading && uiState.rank.isEmpty() -> LoadingBox(Modifier.fillMaxSize())
        uiState.rankError != null && uiState.rank.isEmpty() -> ErrorBox(
            message = uiState.rankError,
            onRetry = viewModel::loadRank,
            modifier = Modifier.fillMaxSize()
        )
        else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(uiState.rank) { item ->
                SeasonCard(
                    item = item,
                    onClick = {
                        // 排行项只有 bangumi 链接，解析 ss 系列号
                        item.url?.let(::seasonIdFromBangumiUrl)?.let(onSeasonClick)
                    }
                )
            }
        }
    }
}

/** 时间线：新番每日更新（一次性） */
@Composable
private fun AnimeTimelineList(
    onSeasonClick: (Long) -> Unit,
    viewModel: AnimeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    when {
        uiState.timelineLoading && uiState.timeline.isEmpty() -> LoadingBox(Modifier.fillMaxSize())
        uiState.timelineError != null && uiState.timeline.isEmpty() -> ErrorBox(
            message = uiState.timelineError,
            onRetry = viewModel::loadTimeline,
            modifier = Modifier.fillMaxSize()
        )
        else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
            uiState.timeline.forEach { day ->
                item(key = "day-${day.dateTs ?: day.date}") {
                    Text(
                        text = buildString {
                            append(day.date.orEmpty())
                            if (day.isToday == 1) append(" · 今天")
                        },
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 12.dp, top = 14.dp, bottom = 4.dp)
                    )
                }
                items(day.episodes.orEmpty(), key = { "ep-${it.seasonId}-${it.episodeId}" }) { episode ->
                    SeasonCard(
                        item = episode,
                        onClick = { episode.seasonId?.let(onSeasonClick) }
                    )
                }
            }
        }
    }
}

@Composable
internal fun LoadingBox(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
internal fun ErrorBox(
    message: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message ?: "加载失败",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onRetry) {
                Text(text = "重试")
            }
        }
    }
}