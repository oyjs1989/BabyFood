package com.example.babyfood.presentation.ui.common

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * 应用标题栏组件
 *
 * @deprecated 此组件已弃用。AppScaffold 已移除顶部标题栏以提供更多内容空间。
 * 请使用全局 Header（AppHeader）中的"BabyFood"应用名称进行导航。
 *
 * 基于 Material 3 TopAppBar，支持标题、返回按钮和操作按钮
 *
 * @deprecated 此组件已弃用。AppScaffold 已移除顶部标题栏以提供更多内容空间。
 * 请使用全局 Header（AppHeader）中的"BabyFood"应用名称进行导航。
 *
 * @param appBarConfig 标题栏配置
 * @param onBackClick 返回按钮点击事件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopAppBar(
    appBarConfig: AppBarConfig,
    onBackClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = appBarConfig.title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            if (appBarConfig.showBackButton) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .semantics { contentDescription = "返回" }
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                ) {
                    Icon(
                        imageVector = appBarConfig.backIcon,
                        contentDescription = "返回",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        actions = {
            appBarConfig.actions.forEach { action ->
                IconButton(
                    onClick = action.onClick,
                    enabled = action.enabled,
                    modifier = Modifier
                        .semantics { contentDescription = action.contentDescription }
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.contentDescription,
                        tint = if (action.enabled) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        windowInsets = WindowInsets(0, 0, 0, 0)
    )
}