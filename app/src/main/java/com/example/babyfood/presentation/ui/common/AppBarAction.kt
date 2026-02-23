package com.example.babyfood.presentation.ui.common

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 标题栏操作按钮
 *
 * @property icon 按钮图标（必需）
 * @property contentDescription 无障碍访问描述（必需）
 * @property onClick 点击事件处理器（必需）
 * @property enabled 是否启用（默认：true）
 */
@Immutable
data class AppBarAction(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit,
    val enabled: Boolean = true
)