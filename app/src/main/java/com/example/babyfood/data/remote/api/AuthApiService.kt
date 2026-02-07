package com.example.babyfood.data.remote.api

import com.example.babyfood.data.remote.dto.RegisterRequest
import com.example.babyfood.data.remote.dto.RegisterResponse
import com.example.babyfood.data.remote.dto.VerificationCodeResponse
import com.example.babyfood.domain.model.LoginRequest
import com.example.babyfood.domain.model.LoginResponse
import com.example.babyfood.domain.model.LogoutRequest
import com.example.babyfood.domain.model.LogoutResponse
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
     * 发送短信验证码
     * @param phone 手机号
     * @return 验证码发送响应
     */
    @POST("auth/verification-code/sms")
    suspend fun sendSmsVerificationCode(
        @Body phone: String
    ): VerificationCodeResponse

    /**
     * 发送邮件验证码
     * @param email 邮箱地址
     * @return 验证码发送响应
     */
    @POST("auth/verification-code/email")
    suspend fun sendEmailVerificationCode(
        @Body email: String
    ): VerificationCodeResponse

    /**
     * 用户注册
     * @param request 注册请求
     * @return 注册响应（注册成功后自动登录）
     */
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

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
     * @param request 登出请求
     * @return 登出响应
     */
    @POST("auth/logout")
    suspend fun logout(
        @Body request: LogoutRequest
    ): LogoutResponse
}