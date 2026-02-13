package com.example.babyfood.domain.model

import kotlinx.serialization.Serializable

/**
 * 登录响应模型
 */
@Serializable
data class LoginResponse(
    override val success: Boolean,
    override val errorMessage: String? = null,
    val errorCode: String? = null,
    val token: String? = null,
    val refreshToken: String? = null,
    val user: User? = null,
    val expiresIn: Long = 0
) : ApiResponse