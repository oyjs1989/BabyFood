package com.example.babyfood.data.remote.dto

import com.example.babyfood.domain.model.User
import kotlinx.serialization.Serializable

/**
 * 用户注册响应
 *
 * @property success 是否成功
 * @property errorMessage 错误消息
 * @property errorCode 错误代码
 * @property token 访问令牌
 * @property refreshToken 刷新令牌
 * @property user 用户信息
 * @property expiresIn 过期时间（秒）
 */
@Serializable
data class RegisterResponse(
    val success: Boolean,
    val errorMessage: String? = null,
    val errorCode: String? = null,
    val token: String? = null,
    val refreshToken: String? = null,
    val user: User? = null,
    val expiresIn: Long? = null
)