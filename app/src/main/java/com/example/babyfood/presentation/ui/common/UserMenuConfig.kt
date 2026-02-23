package com.example.babyfood.presentation.ui.common

/**
 * 用户菜单配置数据类
 *
 * 定义用户菜单的配置
 *
 * @property menuItems 菜单项列表（必填，非空）
 * @property showAvatar 是否显示用户头像（默认 true）
 * @property onAvatarClick 头像点击回调（可选）
 */
data class UserMenuConfig(
    val menuItems: List<UserMenuItem>,
    val showAvatar: Boolean = true,
    val onAvatarClick: (() -> Unit)? = null
)