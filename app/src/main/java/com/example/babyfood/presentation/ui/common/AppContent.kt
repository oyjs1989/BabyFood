package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

/**
 * 应用内容区域组件
 *
 * 提供内容区域，支持滚动和响应式布局
 *
 * @param modifier 修饰符
 * @param content 内容
 */
@Composable
fun AppContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // 在大屏幕上限制内容最大宽度
    val maxContentWidth = if (screenWidth > 600.dp) {
        600.dp
    } else {
        screenWidth
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .widthIn(max = maxContentWidth),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
        ) {
            content()
        }
    }
}