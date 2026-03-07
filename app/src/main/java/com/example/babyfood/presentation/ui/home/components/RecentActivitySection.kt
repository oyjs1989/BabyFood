package com.example.babyfood.presentation.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.babyfood.presentation.theme.Blue
import com.example.babyfood.presentation.theme.Green
import com.example.babyfood.presentation.theme.Primary

/**
 * 最近活动记录区域 - 参考设计中的活动列表
 */
@Composable
fun RecentActivitySection(
    activities: List<ActivityItem> = getSampleActivities(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        activities.forEachIndexed { index, activity ->
            ActivityRow(
                activity = activity,
                showConnector = index < activities.size - 1
            )
        }
    }
}

@Composable
private fun ActivityRow(
    activity: ActivityItem,
    showConnector: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 左侧图标和时间线
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 图标容器
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = activity.iconBackgroundColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = activity.icon,
                    contentDescription = null,
                    tint = activity.iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            // 连接线
            if (showConnector) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(20.dp)
                        .background(
                            color = Color(0xFFE5E7EB),
                            shape = RoundedCornerShape(1.dp)
                        )
                )
            }
        }

        // 右侧内容
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F2937)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = activity.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = activity.time,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9CA3AF),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * 活动项数据类
 */
data class ActivityItem(
    val title: String,
    val description: String,
    val time: String,
    val icon: ImageVector,
    val iconColor: Color,
    val iconBackgroundColor: Color
)

/**
 * 示例活动数据
 */
private fun getSampleActivities(): List<ActivityItem> {
    return listOf(
        ActivityItem(
            title = "新食材识别",
            description = "通过 AI 相机识别到：蒸西兰花",
            time = "2小时前",
            icon = Icons.Default.Restaurant,
            iconColor = Color(0xFFEA580C), // Orange 600
            iconBackgroundColor = Color(0xFFFFEDD5) // Orange 100
        ),
        ActivityItem(
            title = "生长里程碑",
            description = "体重记录：8.4kg（高于平均水平！）",
            time = "昨天",
            icon = Icons.Default.TrendingUp,
            iconColor = Green,
            iconBackgroundColor = Color(0xFFDCFCE7) // Green 100
        )
    )
}

/**
 * 活动记录卡片容器
 */
@Composable
fun RecentActivityCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            content()
        }
    }
}
