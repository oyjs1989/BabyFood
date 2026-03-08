package com.example.babyfood.presentation.ui.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babyfood.domain.model.NutritionGoal
import com.example.babyfood.domain.model.NutritionIntake
import com.example.babyfood.presentation.theme.AvocadoDark
import com.example.babyfood.presentation.theme.AvocadoPrimary
import com.example.babyfood.presentation.theme.Charcoal
import com.example.babyfood.presentation.theme.Coral
import com.example.babyfood.presentation.theme.Cream
import com.example.babyfood.presentation.theme.Gray300
import com.example.babyfood.presentation.theme.Peach
import com.example.babyfood.presentation.theme.SoftBrown

/**
 * 营养目标卡片 - 横向滚动的圆形进度条设计
 * 展示：热量、蛋白质、钙、铁
 */
@Composable
fun NutritionGoalCard(
    nutritionGoal: NutritionGoal,
    nutritionIntake: NutritionIntake = NutritionIntake.empty(),
    onEdit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 标题行
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "NUTRITION OVERVIEW",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = SoftBrown
            )
        }

        // 横向滚动的营养指标卡片
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.width(4.dp))

            // 热量卡片 - Peach色
            CircularProgressCard(
                label = "热量",
                currentValue = nutritionIntake.calories.toInt(),
                targetValue = nutritionGoal.calories.toInt(),
                unit = "kcal",
                progressColor = Peach,
                icon = Icons.Default.LocalFireDepartment,
                onClick = onEdit
            )

            // 蛋白质卡片 - Primary色(AvocadoPrimary)
            CircularProgressCard(
                label = "蛋白质",
                currentValue = nutritionIntake.protein.toInt(),
                targetValue = nutritionGoal.protein.toInt(),
                unit = "g",
                progressColor = AvocadoPrimary,
                icon = Icons.Default.Egg,
                onClick = onEdit
            )

            // 铁卡片 - Coral色
            CircularProgressCard(
                label = "铁",
                currentValue = nutritionIntake.iron.toInt(),
                targetValue = nutritionGoal.iron.toInt(),
                unit = "mg",
                progressColor = Coral,
                textIcon = "Fe",
                onClick = onEdit
            )

            // 钙卡片 - Peach色
            CircularProgressCard(
                label = "钙",
                currentValue = nutritionIntake.calcium.toInt(),
                targetValue = nutritionGoal.calcium.toInt(),
                unit = "mg",
                progressColor = Peach,
                textIcon = "Ca",
                onClick = onEdit
            )

            Spacer(modifier = Modifier.width(4.dp))
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 水分摄入大卡片
        WaterIntakeCard(
            currentWater = 350,
            targetWater = 500,
            onClick = onEdit,
            onAddWater = { /* TODO: 添加水分记录 */ }
        )
    }
}

/**
 * 水分摄入大卡片 - 渐变边框设计
 */
@Composable
private fun WaterIntakeCard(
    currentWater: Int,
    targetWater: Int,
    onClick: () -> Unit = {},
    onAddWater: () -> Unit = {}
) {
    val progress = if (targetWater > 0) {
        (currentWater.toFloat() / targetWater).coerceIn(0f, 1f)
    } else 0f

    // 渐变边框背景 - 从Cream到Peach
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Cream, Peach.copy(alpha = 0.5f))
                )
            )
            .padding(3.dp) // 渐变边框宽度
    ) {
        // 白色内部卡片
        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(21.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧：圆形进度条 + 信息
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 圆形进度条 + 水滴图标
                    Box(
                        modifier = Modifier.size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.Canvas(
                            modifier = Modifier.size(48.dp)
                        ) {
                            val strokeWidth = 3.dp.toPx()
                            val diameter = size.minDimension - strokeWidth
                            val radius = diameter / 2f
                            val centerOffset = Offset(size.width / 2f, size.height / 2f)

                            // 背景圆环 - Gray300
                            drawCircle(
                                color = Gray300,
                                radius = radius,
                                center = centerOffset,
                                style = Stroke(width = strokeWidth)
                            )

                            // 进度圆弧 - 从顶部开始
                            if (progress > 0f) {
                                drawArc(
                                    color = AvocadoPrimary,
                                    startAngle = -90f,
                                    sweepAngle = 360f * progress,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                    topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                                    size = Size(diameter, diameter)
                                )
                            }
                        }

                        // 水滴图标
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = AvocadoPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // 文字信息
                    Column {
                        Text(
                            text = "水分摄入",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Charcoal
                        )
                        Text(
                            text = "${currentWater}ml / ${targetWater}ml 目标",
                            style = MaterialTheme.typography.bodySmall,
                            color = SoftBrown,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // 右侧：添加按钮 + 箭头
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 添加按钮
                    IconButton(
                        onClick = onAddWater,
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = AvocadoPrimary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "添加水分",
                            tint = AvocadoDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // 箭头
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = SoftBrown.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CircularProgressCard(
    label: String,
    currentValue: Int,
    targetValue: Int,
    unit: String,
    progressColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    textIcon: String? = null,
    onClick: () -> Unit = {}
) {
    val progress = if (targetValue > 0) {
        (currentValue.toFloat() / targetValue).coerceIn(0f, 1f)
    } else 0f
    val percentage = (progress * 100).toInt()

    Card(
        onClick = onClick,
        modifier = Modifier.width(110.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Cream
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 标题 - 10px, 大写
            Text(
                text = label.uppercase(),
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = SoftBrown
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 圆形进度条 - 使用固定尺寸的 Canvas
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.size(56.dp)
                ) {
                    val strokeWidth = 4.dp.toPx()
                    val diameter = size.minDimension - strokeWidth
                    val radius = diameter / 2f
                    val centerOffset = Offset(size.width / 2f, size.height / 2f)

                    // 背景圆环 - 使用淡灰色，在 Cream 背景上可见
                    drawCircle(
                        color = Color.White,
                        radius = radius,
                        center = centerOffset,
                        style = Stroke(width = strokeWidth)
                    )

                    // 进度圆弧 - 从顶部开始（-90度）
                    if (progress > 0f) {
                        drawArc(
                            color = progressColor,
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                            size = Size(diameter, diameter)
                        )
                    }
                }

                // 中心百分比 - 14px
                Text(
                    text = "$percentage%",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Charcoal
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 数值行 - 9px
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = progressColor,
                        modifier = Modifier.size(12.dp)
                    )
                } else if (textIcon != null) {
                    Text(
                        text = textIcon,
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = progressColor
                    )
                }

                Text(
                    text = "$currentValue/$targetValue $unit",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = SoftBrown
                )
            }
        }
    }
}