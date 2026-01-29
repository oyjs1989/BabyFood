package com.example.babyfood.presentation.ui.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.babyfood.presentation.theme.AnimationDurationNormal
import com.example.babyfood.presentation.theme.AnimationEasing

/**
 * 环形进度条组件
 *
 * @param progress 进度（0.0-1.0）
 * @param modifier 修饰符
 * @param size 进度条大小
 * @param strokeWidth 进度条宽度
 * @param backgroundColor 背景圆环颜色
 * @param progressColor 进度条颜色
 * @param animationDuration 动画持续时间（毫秒）
 */
@Composable
fun CircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    strokeWidth: Dp = 6.dp,
    backgroundColor: Color = Color(0xFFE0E0E0),
    progressColor: Color = Color(0xFF42A5F5),
    animationDuration: Int = AnimationDurationNormal
) {
    val animatedProgress = animateProgress(progress, animationDuration)

    Canvas(
        modifier = modifier.size(size)
    ) {
        drawCircularProgress(
            size = size,
            strokeWidth = strokeWidth,
            backgroundColor = backgroundColor,
            progressColor = progressColor,
            animatedProgress = animatedProgress
        )
    }
}

/**
 * 带数值的环形进度条组件
 *
 * @param progress 进度（0.0-1.0）
 * @param value 显示的数值
 * @param unit 单位
 * @param label 标签
 * @param modifier 修饰符
 * @param size 进度条大小
 * @param strokeWidth 进度条宽度
 * @param progressColor 进度条颜色
 * @param animationDuration 动画持续时间
 */
@Composable
fun CircularProgressWithValue(
    progress: Float,
    value: String,
    unit: String,
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    strokeWidth: Dp = 8.dp,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    animationDuration: Int = AnimationDurationNormal
) {
    val animatedProgress = animateProgress(progress, animationDuration)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(size)
        ) {
            drawCircularProgress(
                size = size,
                strokeWidth = strokeWidth,
                backgroundColor = progressColor.copy(alpha = 0.15f),
                progressColor = progressColor,
                animatedProgress = animatedProgress
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = progressColor
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 创建并运行动画
 */
@Composable
private fun animateProgress(
    targetProgress: Float,
    duration: Int
): Animatable<Float, AnimationVector1D> {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(targetProgress) {
        animatedProgress.animateTo(
            targetValue = targetProgress.coerceIn(0f, 1f),
            animationSpec = tween(
                durationMillis = duration,
                easing = AnimationEasing
            )
        )
    }

    return animatedProgress
}

/**
 * 绘制环形进度
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCircularProgress(
    size: Dp,
    strokeWidth: Dp,
    backgroundColor: Color,
    progressColor: Color,
    animatedProgress: Animatable<Float, AnimationVector1D>
) {
    val canvasSize = size.toPx()
    val center = Offset(canvasSize / 2, canvasSize / 2)
    val radius = (canvasSize - strokeWidth.toPx()) / 2
    val stroke = Stroke(width = strokeWidth.toPx())

    // 绘制背景圆环
    drawArc(
        color = backgroundColor,
        startAngle = -90f,
        sweepAngle = 360f,
        useCenter = false,
        style = stroke,
        size = Size(radius * 2, radius * 2),
        topLeft = Offset(center.x - radius, center.y - radius)
    )

    // 绘制进度圆环
    if (animatedProgress.value > 0f) {
        drawArc(
            color = progressColor,
            startAngle = -90f,
            sweepAngle = 360f * animatedProgress.value,
            useCenter = false,
            style = stroke,
            size = Size(radius * 2, radius * 2),
            topLeft = Offset(center.x - radius, center.y - radius)
        )
    }
}