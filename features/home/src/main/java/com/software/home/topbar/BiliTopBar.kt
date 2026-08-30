package com.software.home.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.software.designsystem.BiliColors

/**
 * 首页顶部音乐分类栏。
 * 选中状态提升到父组件（状态提升），由父组件决定切换哪块内容。
 */
@Composable
fun BiliTopBar(
    tabs: List<String> = defaultMusicTabs,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onMenuClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = BiliColors.TextPrimary
            )
        }
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.weight(1f),
            containerColor = Color.Transparent,
            contentColor = BiliColors.TextPrimary,
            edgePadding = 0.dp,
            divider = {},
            indicator = { tabPositions ->
                if (selectedTabIndex < tabPositions.size) {
                    val currentTabPosition = tabPositions[selectedTabIndex]
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(currentTabPosition)
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(BiliColors.BiliPink)
                        )
                    }
                }
            }
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTabIndex == index
                Tab(
                    selected = isSelected,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier
                        .height(48.dp)
                        .width(24.dp)
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    content = {
                        Text(
                            text = title,
                            fontSize = if (isSelected) 18.sp else 16.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) BiliColors.TextPrimary else BiliColors.TextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                )
            }
        }
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(28.dp)
                .background(BiliColors.AccentRed, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

val defaultMusicTabs = listOf("直播", "推荐", "热门", "动画", "影视", "新征程")
