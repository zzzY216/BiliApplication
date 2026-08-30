package com.software.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.software.core.model.PopularItem
import com.software.core.model.RecommendItem
import com.software.core.model.VideoDimension
import com.software.home.anime.AnimeTabContent
import com.software.home.anime.ErrorBox
import com.software.home.anime.LoadingBox
import com.software.home.topbar.BiliTopBar
import com.software.home.util.formatShortCount
import kotlin.coroutines.coroutineContext

@Composable
fun BiliHomeRoute(
    onSeasonClick: (Long) -> Unit = {},
    viewModel: BiliHomeViewModel = hiltViewModel(),
) {
    val recommendItems = viewModel.recommendPagingData.collectAsLazyPagingItems()
    val popularItems = viewModel.popularPagingData.collectAsLazyPagingItems()
    BiliHomeScreen(
        recommendItems = recommendItems,
        popularItems = popularItems,
        onSeasonClick = onSeasonClick,
    )
}

@Composable
fun BiliHomeScreen(
    recommendItems: LazyPagingItems<RecommendItem>,
    popularItems: LazyPagingItems<PopularItem>,
    onSeasonClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    // 顶部 Tab 选中状态提升到这里，按 Tab 切换内容（内容槽模式）
    var selectedTab by remember { mutableIntStateOf(0) }
    Column(modifier = modifier.fillMaxSize()) {
        BiliTopBar(
            selectedTabIndex = selectedTab,
            onTabSelected = { selectedTab = it },
        )
        when (selectedTab) {
            // 直播/推荐 → 推荐流（瀑布流）
            0, 1 -> RecommendFeed(
                items = recommendItems,
                modifier = Modifier.weight(1f)
            )
            // 热门 → 热门流（瀑布流）
            2 -> PopularFeed(
                items = popularItems,
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

/** 推荐流：双列瀑布流（封面按真实宽高比参差） */
@Composable
fun RecommendFeed(
    items: LazyPagingItems<RecommendItem>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
    ) {
        items(count = items.itemCount) { index ->
            items[index]?.let { item ->
                BiliVideoUiCard(
                    imageUrl = item.cover,
                    title = item.title,
                    label1 = item.coverLeft1ContentDescription,
                    label2 = buildString {
                        item.coverLeft2ContentDescription?.takeIf { it.isNotBlank() }
                            ?.let { append(it); append(" · ") }
                        item.coverRightContentDescription?.takeIf { it.isNotBlank() }
                            ?.let { append(it) }
                    }.trimEnd(' ', '·'),
                    aspectRatio = item.dimension.aspectRatioOrNull(),
                    onClick = { // TODO
                        Toast.makeText(context, "未完成", Toast.LENGTH_SHORT).show()
                    },
                )
            }
        }
        gridStatusItem(items)
    }
}

/** 热门流：双列瀑布流（分区名/UP 主/播放量） */
@Composable
fun PopularFeed(
    items: LazyPagingItems<PopularItem>,
    modifier: Modifier = Modifier,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
    ) {
        items(count = items.itemCount) { index ->
            items[index]?.let { item ->
                BiliVideoUiCard(
                    imageUrl = item.pic,
                    title = item.title,
                    label1 = item.tname,
                    label2 = "UP：${item.owner.name} · ${formatShortCount(item.stat.view.toLong())}播放",
                    aspectRatio = item.dimension.aspectRatioOrNull(),
                    onClick = { /* TODO M4: 播放器/详情页 */ },
                )
            }
        }
        gridStatusItem(items)
    }
}

/** 瀑布流通用状态项：加载中 / 失败重试（占满整行） */
private fun <T : Any> LazyStaggeredGridScope.gridStatusItem(
    items: LazyPagingItems<T>,
) {
    if (items.itemCount == 0) {
        when (val refresh = items.loadState.refresh) {
            is LoadState.Loading -> item(span = StaggeredGridItemSpan.FullLine) { LoadingBox() }
            is LoadState.Error -> item(span = StaggeredGridItemSpan.FullLine) {
                ErrorBox(
                    message = refresh.error.message,
                    onRetry = { items.retry() }
                )
            }

            else -> Unit
        }
    }
}

/**
 * 通用视频卡片（双列瀑布流用）：
 * 封面按 [aspectRatio] 自适应高度（真实宽高比 → 参差瀑布流效果），下方标题 + 两行元信息。
 */
@Composable
fun BiliVideoUiCard(
    imageUrl: String,
    title: String,
    label1: String?,
    label2: String?,
    aspectRatio: Float?,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio ?: DEFAULT_VIDEO_ASPECT_RATIO),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            label1?.takeIf { it.isNotBlank() }?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            label2?.takeIf { it.isNotBlank() }?.let {
                Spacer(modifier = Modifier.height(2.dp))
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

/** 视频宽高比（封面 16:9 兜底），真实比例夹在 [0.6, 1.8] 避免极端高度 */
private fun VideoDimension?.aspectRatioOrNull(): Float? {
    if (this == null) return null
    val w = width ?: return null
    val h = height ?: return null
    if (w <= 0 || h <= 0) return null
    return (w.toFloat() / h).coerceIn(0.6f, 1.8f)
}

private const val DEFAULT_VIDEO_ASPECT_RATIO = 16f / 9f

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