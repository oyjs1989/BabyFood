package com.example.babyfood.domain.model

/**
 * 认证状态
 */
sealed class AuthState {
    /**
     * 未登录
     */
    data object NotLoggedIn : AuthState()

    /**
     * 登录中
     */
    data object Loading : AuthState()

    /**
     * 已登录
     */
    data class LoggedIn(val user: User) : AuthState()

    /**
     * 登录失败
     */
    data class Error(val message: String) : AuthState()
}