package com.example.babyfood.presentation.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 用户菜单项数据类
 *
 * 定义用户菜单中的菜单项
 *
 * @property id 菜单项唯一标识（必填，非空）
 * @property title 菜单项标题（必填，非空）
 * @property icon 菜单项图标（必填）
 * @property onClick 点击回调（必填）
 * @property enabled 是否启用（默认 true）
 * @property visible 是否可见（默认 true）
 */
data class UserMenuItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
    val visible: Boolean = true
)