package com.example.babyfood.presentation.ui.common

/**
 * Header 配置数据类
 *
 * 定义 Header 的配置参数，控制不同页面的行为
 *
 * @property currentRoute 当前页面路由，用于判断是否为登录/注册页面（可选）
 * @property onAppLogoClick 应用名称点击回调（必填）
 * @property showBackButton 是否显示返回按钮（默认 false）
 * @property pointsBalance 用户积分余额（默认 0）
 * @property onPointsClick 积分图标点击回调（可选）
 */
data class AppHeaderConfig(
    val currentRoute: String? = null,
    val onAppLogoClick: () -> Unit = {},
    val showBackButton: Boolean = false,
    val pointsBalance: Int = 0,
    val onPointsClick: () -> Unit = {}
)