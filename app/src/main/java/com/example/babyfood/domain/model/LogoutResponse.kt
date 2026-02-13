package com.example.babyfood.domain.model

import kotlinx.serialization.Serializable

/**
 * 登出响应模型
 */
@Serializable
data class LogoutResponse(
    override val success: Boolean,
    override val errorMessage: String? = null,
    val errorCode: String? = null
) : ApiResponse