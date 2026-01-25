package com.example.babyfood.domain.model

import kotlinx.serialization.Serializable

/**
 * 注册请求模型
 */
@Serializable
data class RegisterRequest(
    val phone: String? = null,
    val email: String? = null,
    val password: String,
    val confirmPassword: String,
    val verificationCode: String? = null,
    val agreeToTerms: Boolean = false
)