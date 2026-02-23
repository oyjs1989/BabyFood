package com.example.babyfood.presentation.theme.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.babyfood.presentation.theme.ANIMATION_DURATION_LOADING_CYCLE
import com.example.babyfood.presentation.theme.AnimationSpecLoadingInfinite
import com.example.babyfood.presentation.theme.EasingLinear

// ===== 加载指示器 - 1000ms 旋转周期，线性动画，无限循环 =====
// 应用场景：数据加载、异步操作、页面初始化

/**
 * 标准加载指示器
 * @param size 指示器大小，默认 40dp
 * @param strokeWidth 进度条宽度，默认 4dp
 * @param modifier 修饰符
 */
@Composable
fun BabyFoodLoadingIndicator(
    size: Dp = 40.dp,
    strokeWidth: Dp = 4.dp,
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = MaterialTheme.colorScheme.primary,
        strokeWidth = strokeWidth
    )
}

/**
 * 小型加载指示器（用于按钮内部）
 * @param size 指示器大小，默认 20dp
 * @param strokeWidth 进度条宽度，默认 2dp
 * @param modifier 修饰符
 */
@Composable
fun BabyFoodLoadingIndicatorSmall(
    size: Dp = 20.dp,
    strokeWidth: Dp = 2.dp,
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = MaterialTheme.colorScheme.primary,
        strokeWidth = strokeWidth
    )
}

/**
 * 全屏加载指示器（居中显示）
 * @param message 可选的加载提示文字
 * @param modifier 修饰符
 */
@Composable
fun BabyFoodLoadingOverlay(
    message: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            BabyFoodLoadingIndicator()
            
            if (message != null) {
                androidx.compose.material3.Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 自定义旋转加载指示器（使用 AnimationSpecLoadingInfinite）
 * @param size 指示器大小，默认 40dp
 * @param strokeWidth 进度条宽度，默认 4dp
 * @param modifier 修饰符
 */
@Composable
fun BabyFoodRotatingLoadingIndicator(
    size: Dp = 40.dp,
    strokeWidth: Dp = 4.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    
    // 旋转动画：0° → 360°，1000ms 周期，线性动画，无限循环
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = ANIMATION_DURATION_LOADING_CYCLE,
                easing = EasingLinear
            ),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(size)
                .rotate(rotation),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = strokeWidth,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}