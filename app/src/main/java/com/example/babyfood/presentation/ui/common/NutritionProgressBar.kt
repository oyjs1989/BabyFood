package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
 * 营养素进度条
 *
 * 显示单个营养素的摄入进度
 */
@Composable
fun NutritionProgressBar(
    label: String,
    progress: Float,
    target: Float,
    unit: String = "",
    modifier: Modifier = Modifier
) {
    val normalizedProgress = (progress / target).coerceIn(0f, 1.5f)
    val progressColor = when {
        normalizedProgress < 0.8f -> Color(0xFFF44336)  // 红色 - 不足
        normalizedProgress <= 1.2f -> Color(0xFF4CAF50)  // 绿色 - 正常
        else -> Color(0xFFFF9800)  // 橙色 - 过量
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(60.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            LinearProgressIndicator(
                progress = { normalizedProgress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = progressColor,
                trackColor = Color.Transparent
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = String.format("%.0f%s", progress, unit),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = progressColor
        )

        Text(
            text = "/",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = String.format("%.0f%s", target, unit),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 营养素进度卡片
 *
 * 显示所有主要营养素的摄入进度
 */
@Composable
fun NutritionProgressCard(
    caloriesProgress: Float,
    caloriesTarget: Float,
    proteinProgress: Float,
    proteinTarget: Float,
    calciumProgress: Float,
    calciumTarget: Float,
    ironProgress: Float,
    ironTarget: Float,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Card(
        modifier = modifier.fillMaxWidth(),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.outline
        ),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "营养摄入进度",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            NutritionProgressBar(
                label = "热量",
                progress = caloriesProgress,
                target = caloriesTarget,
                unit = "kcal"
            )

            NutritionProgressBar(
                label = "蛋白质",
                progress = proteinProgress,
                target = proteinTarget,
                unit = "g"
            )

            NutritionProgressBar(
                label = "钙",
                progress = calciumProgress,
                target = calciumTarget,
                unit = "mg"
            )

            NutritionProgressBar(
                label = "铁",
                progress = ironProgress,
                target = ironTarget,
                unit = "mg"
            )
        }
    }
}

/**
 * 简化版营养进度条（仅显示进度百分比）
 */
@Composable
fun SimpleNutritionProgressBar(
    label: String,
    progressPercentage: Float,
    modifier: Modifier = Modifier
) {
    val normalizedProgress = progressPercentage.coerceIn(0f, 100f) / 100f
    val progressColor = when {
        progressPercentage < 80f -> Color(0xFFF44336)  // 红色 - 不足
        progressPercentage <= 120f -> Color(0xFF4CAF50)  // 绿色 - 正常
        else -> Color(0xFFFF9800)  // 橙色 - 过量
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(50.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(3.dp)
                )
        ) {
            LinearProgressIndicator(
                progress = { normalizedProgress },
                modifier = Modifier.fillMaxWidth(),
                color = progressColor,
                trackColor = Color.Transparent
            )
        }

        Text(
            text = String.format("%.0f%%", progressPercentage),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = progressColor,
            fontSize = 10.sp
        )
    }
}