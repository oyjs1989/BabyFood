package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 营养汇总卡片
 *
 * 显示营养摄入的总体情况
 */
@Composable
fun NutritionSummaryCard(
    overallScore: Float,
    isBalanced: Boolean,
    caloriesProgress: Float,
    proteinProgress: Float,
    calciumProgress: Float,
    ironProgress: Float,
    modifier: Modifier = Modifier
) {
    val grade = when {
        overallScore >= 90f -> "优秀"
        overallScore >= 75f -> "良好"
        overallScore >= 60f -> "一般"
        overallScore >= 40f -> "较差"
        else -> "很差"
    }

    val gradeColor = when {
        overallScore >= 90f -> Color(0xFF4CAF50)
        overallScore >= 75f -> Color(0xFF8BC34A)
        overallScore >= 60f -> Color(0xFFFFC107)
        overallScore >= 40f -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isBalanced) {
                Color(0xFFE8F5E9)
            } else {
                Color(0xFFFFF3E0)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (isBalanced) {
                        Icons.Default.Check
                    } else {
                        Icons.Default.Info
                    },
                    contentDescription = null,
                    tint = gradeColor,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Text(
                    text = "营养匹配度",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = grade,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = gradeColor
                )

                Text(
                    text = String.format(" %.0f%%", overallScore),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 营养素进度
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
            ) {
                NutritionItem(
                    label = "热量",
                    progress = caloriesProgress,
                    color = getProgressColor(caloriesProgress)
                )
                NutritionItem(
                    label = "蛋白质",
                    progress = proteinProgress,
                    color = getProgressColor(proteinProgress)
                )
                NutritionItem(
                    label = "钙",
                    progress = calciumProgress,
                    color = getProgressColor(calciumProgress)
                )
                NutritionItem(
                    label = "铁",
                    progress = ironProgress,
                    color = getProgressColor(ironProgress)
                )
            }
        }
    }
}

/**
 * 单个营养素项
 */
@Composable
private fun NutritionItem(
    label: String,
    progress: Float,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = String.format("%.0f%%", progress),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 获取进度条颜色
 */
private fun getProgressColor(progress: Float): Color {
    return when {
        progress < 80f -> Color(0xFFF44336)  // 红色 - 不足
        progress <= 120f -> Color(0xFF4CAF50)  // 绿色 - 正常
        else -> Color(0xFFFF9800)  // 橙色 - 过量
    }
}

/**
 * 简化版营养汇总卡片
 */
@Composable
fun SimpleNutritionSummaryCard(
    calories: Float,
    protein: Float,
    calcium: Float,
    iron: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "营养汇总",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
            ) {
                NutritionItem(
                    label = "热量",
                    progress = calories,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                NutritionItem(
                    label = "蛋白质",
                    progress = protein,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                NutritionItem(
                    label = "钙",
                    progress = calcium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                NutritionItem(
                    label = "铁",
                    progress = iron,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}