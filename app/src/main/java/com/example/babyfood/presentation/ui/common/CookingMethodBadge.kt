package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babyfood.data.service.CookingMethodRecommender

/**
 * 制作方式推荐徽章
 *
 * 显示"自制"/"市售"推荐标识
 */
@Composable
fun CookingMethodBadge(
    method: CookingMethodRecommender.CookingMethod,
    modifier: Modifier = Modifier
) {
    val color = Color(
        CookingMethodRecommender().getMethodColor(method).removePrefix("#").toLong(16)
    )
    val icon = CookingMethodRecommender().getMethodIcon(method)
    val text = CookingMethodRecommender().getMethodText(method)

    Box(
        modifier = modifier
            .background(
                color = color,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 制作难度标签
 *
 * 显示制作难度等级（1-5星）
 */
@Composable
fun DifficultyBadge(
    difficultyLevel: Int,
    modifier: Modifier = Modifier
) {
    val stars = when {
        difficultyLevel <= 1 -> "★"
        difficultyLevel <= 2 -> "★★"
        difficultyLevel <= 3 -> "★★★"
        difficultyLevel <= 4 -> "★★★★"
        else -> "★★★★★"
    }

    val color = when {
        difficultyLevel <= 2 -> Color(0xFF4CAF50)  // 绿色-简单
        difficultyLevel <= 4 -> Color(0xFFFF9800)  // 橙色-中等
        else -> Color(0xFFF44336)  // 红色-困难
    }

    Box(
        modifier = modifier
            .background(
                color = color,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "难度:",
                color = Color.White,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stars,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 制作方式推荐卡片
 *
 * 显示完整的制作方式推荐信息
 */
@Composable
fun CookingMethodCard(
    recommendation: CookingMethodRecommender.CookingRecommendation,
    modifier: Modifier = Modifier
) {
    val color = Color(
        CookingMethodRecommender().getMethodColor(recommendation.recommendedMethod).removePrefix("#").toLong(16)
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = CookingMethodRecommender().getMethodIcon(recommendation.recommendedMethod),
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "制作方式推荐",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = CookingMethodRecommender().getMethodText(recommendation.recommendedMethod),
                        style = MaterialTheme.typography.bodyMedium,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                }
                DifficultyBadge(difficultyLevel = recommendation.difficultyLevel)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 推荐理由
            Column {
                Text(
                    text = "推荐理由：",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                recommendation.reasons.forEach { reason ->
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.width(8.dp)
                        )
                        Text(
                            text = reason,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 制作提示
            Column {
                Text(
                    text = "制作提示：",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                recommendation.tips.forEach { tip ->
                    Row(
                        verticalAlignment = Alignment.Top
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
            }
        }
    }
}

/**
 * 简化版制作方式标签
 *
 * 用于空间受限的场景
 */
@Composable
fun SimpleCookingMethodTag(
    method: CookingMethodRecommender.CookingMethod,
    modifier: Modifier = Modifier
) {
    val color = Color(
        CookingMethodRecommender().getMethodColor(method).removePrefix("#").toLong(16)
    )
    val text = when (method) {
        CookingMethodRecommender.CookingMethod.HOMEMADE -> "自制"
        CookingMethodRecommender.CookingMethod.STORE_BOUGHT -> "市售"
        CookingMethodRecommender.CookingMethod.HOMEMADE_OR_STORE -> "自制/市售"
        CookingMethodRecommender.CookingMethod.PROFESSIONAL -> "专业"
    }

    Box(
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