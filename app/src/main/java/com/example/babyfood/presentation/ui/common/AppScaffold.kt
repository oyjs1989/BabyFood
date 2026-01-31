package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp

/**
 * 应用主布局组件
 *
 * 实现简化的 body-footer 布局结构（移除顶部标题栏）
 * - Body: 内容区域（直接从屏幕顶部开始）
 * - Footer: AppBottomBar（固定在底部）
 *
 * 注意：此组件已移除顶部标题栏（AppTopAppBar），以提供更多内容空间。
 * 用户应使用全局 Header（AppHeader）中的"BabyFood"应用名称返回首页。
 *
 * @param bottomActions 底部操作按钮列表
 * @param modifier 修饰符
 * @param content 内容
 */
@Composable
fun AppScaffold(
    bottomActions: List<AppBottomAction> = emptyList(),
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        modifier = modifier
            .semantics { testTag = "app_scaffold" },
        // 移除 topBar 参数，Scaffold 将不再显示顶部标题栏
        bottomBar = {
            AppBottomBar(bottomActions = bottomActions)
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            content()
        }
    }
}