package com.example.babyfood.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * 验证码发送响应
 *
 * @property success 是否成功
 * @property errorMessage 错误消息
 * @property errorCode 错误代码
 * @property expiresIn 过期时间（秒）
 */
@Serializable
data class VerificationCodeResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val errorCode: String? = null,
    val expiresIn: Long? = null
)