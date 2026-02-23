package com.example.babyfood.domain.model

import kotlinx.serialization.Serializable

/**
 * 登出请求模型
 */
@Serializable
data class LogoutRequest(
    val token: String? = null
)