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
 * 实现统一的 header-body-footer 布局结构
 * - Header: AppTopAppBar（固定在顶部）
 * - Body: AppContent（可滚动的内容区域）
 * - Footer: AppBottomBar（固定在底部）
 *
 * @param appBarConfig 标题栏配置
 * @param bottomActions 底部操作按钮列表
 * @param modifier 修饰符
 * @param onBackClick 返回按钮点击事件
 * @param snackbarHostState Snackbar 宿主状态
 * @param content 内容
 */
@Composable
fun AppScaffold(
    appBarConfig: AppBarConfig,
    bottomActions: List<AppBottomAction> = emptyList(),
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        modifier = modifier
            .semantics { testTag = "app_scaffold" },
        topBar = {
            AppTopAppBar(
                appBarConfig = appBarConfig,
                onBackClick = onBackClick
            )
        },
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