package com.example.babyfood.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * 用户注册请求
 *
 * @property phone 手机号（可选）
 * @property email 邮箱（可选）
 * @property password 密码
 * @property confirmPassword 确认密码
 * @property verificationCode 验证码
 * @property agreeToTerms 是否同意服务条款
 * @property deviceInfo 设备信息
 */
@Serializable
data class RegisterRequest(
    val phone: String? = null,
    val email: String? = null,
    val password: String,
    val confirmPassword: String,
    val verificationCode: String,
    val agreeToTerms: Boolean,
    val deviceInfo: DeviceInfo
)

/**
 * 设备信息
 *
 * @property platform 平台（android/ios）
 * @property device_id 设备唯一标识
 * @property app_version 应用版本
 */
@Serializable
data class DeviceInfo(
    val platform: String = "android",
    val device_id: String,
    val app_version: String
)