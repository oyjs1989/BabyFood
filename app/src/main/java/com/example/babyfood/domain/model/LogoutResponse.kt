package com.example.babyfood.domain.model

import kotlinx.serialization.Serializable

/**
 * 登出响应模型
 */
@Serializable
data class LogoutResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val errorCode: String? = null
)