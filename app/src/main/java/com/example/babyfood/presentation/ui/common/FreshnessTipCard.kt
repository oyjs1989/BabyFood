package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babyfood.data.service.FreshnessAdvisor

/**
 * 新鲜度提示卡片
 *
 * 显示食材的新鲜度建议
 */
@Composable
fun FreshnessTipCard(
    advice: FreshnessAdvisor.FreshnessAdvice,
    modifier: Modifier = Modifier
) {
    val (icon, title, backgroundColor) = when (advice.level) {
        FreshnessAdvisor.FreshnessLevel.FRESH -> {
            Triple("🥬", "新鲜建议", Color(0xFFE8F5E9))
        }
        FreshnessAdvisor.FreshnessLevel.FROZEN_RECOMMENDED -> {
            Triple("❄️", "推荐冷冻", Color(0xFFE3F2FD))
        }
        FreshnessAdvisor.FreshnessLevel.CANNED_ACCEPTABLE -> {
            Triple("🥫", "可接受罐装", Color(0xFFF3E5F5))
        }
        FreshnessAdvisor.FreshnessLevel.CONSIDER_EXPIRY -> {
            Triple("⚠️", "注意保质期", Color(0xFFFFF3E0))
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = advice.ingredientName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            advice.tips.forEach { tip ->
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.Top
                ) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(8.dp)
                    )
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = "💡",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = advice.storageAdvice,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 新鲜度摘要卡片
 *
 * 显示整个食谱的新鲜度摘要
 */
@Composable
fun FreshnessSummaryCard(
    summary: String,
    adviceCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = "🥬",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "新鲜度建议",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "$adviceCount 项",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

/**
 * 保质期提示标签
 *
 * 显示食材的保质期信息
 */
@Composable
fun StorageDaysTag(
    days: Int,
    modifier: Modifier = Modifier
) {
    val (color, text) = when {
        days <= 1 -> {
            Pair(Color(0xFFFF5252), "当天食用")
        }
        days <= 3 -> {
            Pair(Color(0xFFFF9800), "3天内")
        }
        days <= 7 -> {
            Pair(Color(0xFFFFC107), "1周内")
        }
        else -> {
            Pair(Color(0xFF4CAF50), "${days}天")
        }
    }

    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .background(
                color = color,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}