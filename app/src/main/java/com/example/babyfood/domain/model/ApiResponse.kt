package com.example.babyfood.domain.model

import kotlinx.serialization.Serializable

/**
 * 通用 API 响应接口
 * 所有 API 响应类都应该实现此接口
 */
interface ApiResponse {
    val success: Boolean
    val errorMessage: String?

    /**
     * 检查是否成功
     */
    fun isSuccess(): Boolean = success

    /**
     * 检查是否失败
     */
    fun isError(): Boolean = !success

    /**
     * 获取错误信息或null
     */
    fun errorOrNull(): String? = errorMessage

    /**
     * 要求成功，否则抛出异常
     */
    fun requireSuccess(): Boolean {
        if (!success) {
            throw ApiException(errorMessage ?: "操作失败")
        }
        return true
    }

    /**
     * 要求成功，否则抛出异常（带自定义消息）
     */
    fun requireSuccessOrThrow(message: String): Boolean {
        if (!success) {
            throw ApiException(message)
        }
        return true
    }
}

/**
 * 通用响应包装类
 * 用于简单的成功/失败响应
 */
@Serializable
data class SimpleResponse(
    override val success: Boolean = true,
    override val errorMessage: String? = null
) : ApiResponse

/**
 * API 响应扩展函数
 */
inline fun <T : ApiResponse> T.onSuccess(action: () -> Unit): T {
    if (success) action()
    return this
}

inline fun <T : ApiResponse> T.onError(action: (String) -> Unit): T {
    val msg = errorMessage
    if (!success && msg != null) {
        action(msg)
    }
    return this
}

/**
 * API 异常
 */
class ApiException(message: String, val code: String? = null) : Exception(message)
