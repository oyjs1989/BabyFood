package com.example.babyfood.presentation.theme.components

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
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.babyfood.presentation.theme.CardBackground
import com.example.babyfood.presentation.theme.NutritionGradientEnd
import com.example.babyfood.presentation.theme.NutritionGradientStart
import com.example.babyfood.presentation.theme.SpacingSM
import com.example.babyfood.presentation.theme.TextSecondary

// ===== 营养进度条组件 =====
// 应用场景：营养目标进度显示、营养摄入统计
// 样式：水平渐变从 #88C999 到 #52C41A

/**
 * 营养进度条
 * @param progress 进度值（0.0-1.0）
 * @param label 标签文字
 * @param modifier 修饰符
 * @param height 进度条高度，默认 8dp
 * @param showPercentage 是否显示百分比，默认 true
 */
@Composable
fun BabyFoodProgressBar(
    progress: Float,
    label: String? = null,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    showPercentage: Boolean = true
) {
    Column(
        modifier = modifier
    ) {
        // 标签行（如果提供了标签）
        if (label != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (showPercentage) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(SpacingSM))
        }

        // 进度条
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            color = ProgressIndicatorDefaults.linearColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )
    }
}

/**
 * 营养进度条（带目标值和当前值）
 * @param current 当前值
 * @param target 目标值
 * @param label 标签文字
 * @param unit 单位
 * @param modifier 修饰符
 */
@Composable
fun BabyFoodNutritionProgressBar(
    current: Float,
    target: Float,
    label: String,
    unit: String = "g",
    modifier: Modifier = Modifier
) {
    val progress = (current / target).coerceIn(0f, 1f)

    Column(
        modifier = modifier
    ) {
        // 标签行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${current}${unit}/${target}${unit}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(SpacingSM))

        // 进度条（带渐变）
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(8.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                NutritionGradientStart,
                                NutritionGradientEnd
                            )
                        ),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

/**
 * 双重进度条（用于显示当前值和目标值的对比）
 * @param current 当前值
 * @param target 目标值
 * @param label 标签文字
 * @param unit 单位
 * @param modifier 修饰符
 */
@Composable
fun BabyFoodDualProgressBar(
    current: Float,
    target: Float,
    label: String,
    unit: String = "g",
    modifier: Modifier = Modifier
) {
    val currentProgress = (current / target).coerceIn(0f, 1f)

    Column(
        modifier = modifier
    ) {
        // 标签行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${current}${unit}/${target}${unit}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(SpacingSM))

        // 双重进度条
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(2.dp)
        ) {
            // 目标进度（灰色）
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        color = CardBackground,
                        shape = RoundedCornerShape(4.dp)
                    )
            )

            // 当前进度（渐变）
            Box(
                modifier = Modifier
                    .fillMaxWidth(currentProgress)
                    .height(8.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                NutritionGradientStart,
                                NutritionGradientEnd
                            )
                        ),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

/**
 * 圆形进度条
 * @param progress 进度值（0.0-1.0）
 * @param label 标签文字
 * @param modifier 修饰符
 */
@Composable
fun BabyFoodCircularProgressBar(
    progress: Float,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.width(64.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 6.dp,
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(SpacingSM))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * 营养进度组（显示多个营养素的进度）
 * @param nutritionData 营养数据列表
 * @param modifier 修饰符
 */
@Composable
fun BabyFoodNutritionProgressGroup(
    nutritionData: List<NutritionProgressData>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        nutritionData.forEach { data ->
            BabyFoodNutritionProgressBar(
                current = data.current,
                target = data.target,
                label = data.label,
                unit = data.unit
            )
        }
    }
}

/**
 * 营养进度数据类
 */
data class NutritionProgressData(
    val label: String,
    val current: Float,
    val target: Float,
    val unit: String = "g"
)