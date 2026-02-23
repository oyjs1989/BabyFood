package com.example.babyfood.presentation.ui.inventory

import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

/**
 * AI 图片识别加载对话框
 * 显示带有旋转动画的加载遮罩层
 */
@Composable
fun ImageRecognitionLoadingDialog(
    imageUri: Uri? = null
) {
    // 旋转动画 - 外圈
    var rotation by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            rotation = (rotation + 10f) % 360f
            delay(30)
        }
    }

    // 旋转动画 - 内圈（反向）
    var rotation2 by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            rotation2 = (rotation2 - 15f) % 360f
            delay(25)
        }
    }

    // 文字透明度动画（闪烁效果）
    var textAlpha by remember { mutableStateOf(1f) }
    LaunchedEffect(Unit) {
        var fading = true
        while (true) {
            if (fading) {
                textAlpha -= 0.1f
                if (textAlpha <= 0.5f) fading = false
            } else {
                textAlpha += 0.1f
                if (textAlpha >= 1f) fading = true
            }
            delay(150)
        }
    }

    // 动态点动画
    var dotCount by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            dotCount = (dotCount + 1) % 4
            delay(500)
        }
    }

    val primaryColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
    val secondaryColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary
    val surfaceColor = androidx.compose.material3.MaterialTheme.colorScheme.surface

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = surfaceColor,
            shadowElevation = 8.dp,
            modifier = Modifier.padding(32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(vertical = 20.dp)
            ) {
                Text(
                    text = "AI 识别中",
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                imageUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "正在识别的食材图片",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 双圈旋转动画
                Box(
                    modifier = Modifier.size(70.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // 外圈 - 正向旋转
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 5f
                        val diameter = size.minDimension - strokeWidth
                        val radius = diameter / 2
                        val centerX = size.width / 2
                        val centerY = size.height / 2

                        drawArc(
                            color = primaryColor,
                            startAngle = rotation - 90f,
                            sweepAngle = 80f,
                            useCenter = false,
                            topLeft = androidx.compose.ui.geometry.Offset(centerX - radius, centerY - radius),
                            size = androidx.compose.ui.geometry.Size(diameter, diameter),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    // 内圈 - 反向旋转
                    Canvas(modifier = Modifier.size(50.dp)) {
                        val strokeWidth = 5f
                        val diameter = size.minDimension - strokeWidth
                        val radius = diameter / 2
                        val centerX = size.width / 2
                        val centerY = size.height / 2

                        drawArc(
                            color = secondaryColor,
                            startAngle = rotation2 - 90f,
                            sweepAngle = 100f,
                            useCenter = false,
                            topLeft = androidx.compose.ui.geometry.Offset(centerX - radius, centerY - radius),
                            size = androidx.compose.ui.geometry.Size(diameter, diameter),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val dots = ".".repeat(dotCount)
                androidx.compose.material3.Text(
                    text = "正在智能识别食材信息$dots",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = textAlpha)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "AI 正在分析图片特征",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}