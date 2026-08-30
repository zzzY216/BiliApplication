package com.software.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.software.core.model.RecommendItem
import com.software.core.model.navigation.AnimeDetailRoute
import com.software.home.anime.AnimeTabContent
import com.software.home.topbar.MusicTopBar

@Composable
fun BiliHomeRoute(
    onSeasonClick: (Long) -> Unit = {},
    viewModel: BiliHomeViewModel = hiltViewModel(),
) {
    val items = viewModel.recommendPagingData.collectAsLazyPagingItems()
    BiliHomeScreen(
        items = items,
        onSeasonClick = onSeasonClick,
    )
}

@Composable
fun BiliHomeScreen(
    items: LazyPagingItems<RecommendItem>,
    onSeasonClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    // 顶部 Tab 选中状态提升到这里，按 Tab 切换内容（内容槽模式）
    var selectedTab by remember { mutableIntStateOf(0) }
    Column(modifier = modifier.fillMaxSize()) {
        MusicTopBar(
            selectedTabIndex = selectedTab,
            onTabSelected = { selectedTab = it },
        )
        when (selectedTab) {
            // 直播/推荐/热门 → 现有推荐流（各 Tab 独立内容为后续迭代）
            0, 1, 2 -> RecommendFeed(
                items = items,
                modifier = Modifier.weight(1f)
            )
            // 动画 → 动漫模块（番剧索引/排行/时间线）
            3 -> AnimeTabContent(
                onSeasonClick = onSeasonClick,
                modifier = Modifier.weight(1f)
            )
            // 影视/新征程 → 占位
            else -> ComingSoon(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ComingSoon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "该模块开发中…",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun RecommendFeed(
    items: LazyPagingItems<RecommendItem>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(count = items.itemCount) { index ->
            items[index]?.let { item ->
                RecommendCard(item = item)
            }
        }
        if (items.itemCount == 0) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun RecommendCard(item: RecommendItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        AsyncImage(
            model = item.cover,
            contentDescription = item.title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = buildString {
                item.coverLeft1ContentDescription?.takeIf { it.isNotBlank() }?.let { append(it); append(" · ") }
                item.coverLeft2ContentDescription?.takeIf { it.isNotBlank() }?.let { append(it); append(" · ") }
                item.coverRightContentDescription?.takeIf { it.isNotBlank() }?.let { append(it) }
            }.trimEnd(' ', '·'),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}