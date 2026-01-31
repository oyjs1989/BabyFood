package com.example.babyfood.domain.model

import kotlinx.serialization.Serializable

/**
 * 用户数据模型
 */
@Serializable
data class User(
    val id: Long = 0,
    val phone: String? = null,
    val email: String? = null,
    val nickname: String = "",
    val avatar: String? = null,
    val createdAt: String = "",
    val updatedAt: String = "",
    val isEmailVerified: Boolean = false,
    val isPhoneVerified: Boolean = false
)