package com.example.babyfood.data.remote.interceptor

import com.example.babyfood.data.local.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response

/**
 * JWT 认证拦截器
 *
 * 自动为所有 HTTP 请求添加 JWT Token 到 Authorization 头
 *
 * @property tokenStorage Token 存储管理器
 */
class JwtAuthInterceptor(
    private val tokenStorage: TokenStorage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 获取当前 Token
        val token = tokenStorage.getToken()

        // 如果有 Token，添加 Authorization 头
        val authenticatedRequest = if (token != null && token.isNotEmpty()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(authenticatedRequest)
    }
}