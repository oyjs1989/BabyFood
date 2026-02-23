package com.example.babyfood.domain.model

import kotlinx.serialization.Serializable

/**
 * 登录请求模型
 * 支持手机号或邮箱登录
 */
@Serializable
data class LoginRequest(
    val account: String, // 手机号或邮箱
    val password: String,
    val rememberMe: Boolean = false,
    val deviceInfo: DeviceInfo? = null
)

/**
 * 设备信息（可选）
 */
@Serializable
data class DeviceInfo(
    val platform: String = "android",
    val deviceId: String = "",
    val appVersion: String = "1.0"
)