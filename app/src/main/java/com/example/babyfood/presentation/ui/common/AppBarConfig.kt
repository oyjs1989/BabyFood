package com.example.babyfood.presentation.ui.common

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

/**
 * 应用标题栏配置
 *
 * @deprecated 此配置类已弃用。AppScaffold 已移除顶部标题栏以提供更多内容空间。
 * 请使用全局 Header（AppHeader）中的"BabyFood"应用名称进行导航。
 *
 * @property title 页面标题（必需）
 * @property showBackButton 是否显示返回按钮（默认：true）
 * @property backIcon 返回按钮图标（默认：Icons.Default.ArrowBack）
 * @property actions 标题栏右侧操作按钮列表（默认：空）
 */
@Immutable
data class AppBarConfig(
    val title: String,
    val showBackButton: Boolean = true,
    val backIcon: ImageVector = Icons.Default.ArrowBack,
    val actions: List<AppBarAction> = emptyList()
)