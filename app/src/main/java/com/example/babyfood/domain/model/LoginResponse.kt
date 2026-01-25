package com.example.babyfood.domain.model

import kotlinx.serialization.Serializable

/**
 * 登录响应模型
 */
@Serializable
data class LoginResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val token: String? = null,
    val refreshToken: String? = null,
    val user: User? = null,
    val expiresIn: Long = 0 // token过期时间（秒）
)