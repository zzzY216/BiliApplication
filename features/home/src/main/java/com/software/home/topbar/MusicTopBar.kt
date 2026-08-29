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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.software.designsystem.BiliColors

/**
 * 首页顶部音乐分类栏（博客/有声书/广播剧/听书）。
 * 原实现硬编码颜色与不可配置回调，这里收敛为 Token + 可注入回调。
 */
@Composable
fun MusicTopBar(
    tabs: List<String> = defaultMusicTabs,
    onMenuClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
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
            selectedTabIndex = selectedIndex,
            modifier = Modifier.weight(1f),
            containerColor = Color.Transparent,
            contentColor = BiliColors.TextPrimary,
            edgePadding = 0.dp,
            divider = {},
            indicator = { tabPositions ->
                if (selectedIndex < tabPositions.size) {
                    val currentTabPosition = tabPositions[selectedIndex]
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
                val isSelected = selectedIndex == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedIndex = index },
                    modifier = Modifier
                        .height(48.dp)
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

val defaultMusicTabs = listOf("博客", "有声书", "广播剧", "听书")
