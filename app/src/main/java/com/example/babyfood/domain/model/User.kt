package com.example.babyfood.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 用户数据模型
 */
@Serializable
data class User(
    val id: Long = 0,
    val username: String = "",
    val phone: String? = null,
    val email: String? = null,
    val nickname: String = "",
    val avatar: String? = null,
    @SerialName("is_email_verified")
    val isEmailVerified: Boolean = false,
    @SerialName("is_phone_verified")
    val isPhoneVerified: Boolean = false,
    @SerialName("created_at")
    val createdAt: Long = 0L,
    @SerialName("updated_at")
    val updatedAt: Long = 0L,
    val role: String = "user",
    val theme: String = "light"
)