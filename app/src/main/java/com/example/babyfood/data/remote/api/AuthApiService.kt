package com.example.babyfood.data.remote.api

import com.example.babyfood.domain.model.LoginRequest
import com.example.babyfood.domain.model.LoginResponse
import com.example.babyfood.domain.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 认证 API 服务接口
 */
interface AuthApiService {

    /**
     * 用户登录
     * 支持手机号或邮箱登录
     * @param request 登录请求
     * @return 登录响应
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    /**
     * 用户注册
     * @param request 注册请求
     * @return 登录响应（注册成功后自动登录）
     */
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): LoginResponse

    /**
     * 刷新令牌
     * @param refreshToken 刷新令牌
     * @return 新的登录响应
     */
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body refreshToken: String
    ): LoginResponse

    /**
     * 用户登出
     * @param token 访问令牌
     */
    @POST("auth/logout")
    suspend fun logout(
        @Body token: String
    )
}