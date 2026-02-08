package com.example.babyfood.data.repository

import android.provider.Settings
import android.util.Log
import com.example.babyfood.data.local.TokenStorage
import com.example.babyfood.data.local.database.dao.UserDao
import com.example.babyfood.data.local.database.entity.UserEntity
import com.example.babyfood.data.remote.api.AuthApiService
import com.example.babyfood.data.remote.dto.RegisterRequest
import com.example.babyfood.data.remote.dto.RegisterResponse
import com.example.babyfood.data.remote.dto.VerificationCodeResponse
import com.example.babyfood.domain.model.AuthState
import com.example.babyfood.domain.model.LoginRequest
import com.example.babyfood.domain.model.LoginResponse
import com.example.babyfood.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import android.content.Context

/**
 * 认证仓库
 * 处理用户登录、注册、登出等认证相关操作
 */
@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val authApiService: AuthApiService,
    private val tokenStorage: TokenStorage,
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "AuthRepository"
        private val PHONE_REGEX = Regex("^1[3-9]\\d{9}$")
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }

    /**
     * 获取认证状态
     */
    fun getAuthState(): Flow<AuthState> =
        userDao.getCurrentUser().map { entity ->
            when {
                entity != null && entity.isLoggedIn -> AuthState.LoggedIn(entity.toDomainModel())
                else -> AuthState.NotLoggedIn
            }
        }

    /**
     * 获取当前登录用户
     */
    fun getCurrentUser(): Flow<User?> =
        userDao.getCurrentUser().map { entity -> entity?.toDomainModel() }

    /**
     * 用户登录
     * @param account 手机号或邮箱
     * @param password 密码
     * @param rememberMe 是否记住我
     */
    suspend fun login(account: String, password: String, rememberMe: Boolean = false): AuthState {
        Log.d(TAG, "========== 开始登录 ==========")
        Log.d(TAG, "账号: $account")
        Log.d(TAG, "记住我: $rememberMe")

        return handleApiCall(
            operationName = "登录",
            request = LoginRequest(
                account = account,
                password = password,
                rememberMe = rememberMe
            ),
            apiCall = { authApiService.login(it) },
            successAction = { response ->
                if (response.success && response.user != null) {
                    val userEntity = response.user.toEntity()
                    userDao.insertUser(userEntity)
                    val loginTime = Clock.System.now().toString()
                    userDao.setLoggedIn(userEntity.id, loginTime)
                    saveToken(response.token, response.refreshToken)
                    Log.d(TAG, "✓ 登录成功")
                    Log.d(TAG, "用户ID: ${response.user.id}")
                    Log.d(TAG, "用户昵称: ${response.user.nickname}")
                    Log.d(TAG, "========== 登录完成 ==========")
                    AuthState.LoggedIn(response.user)
                } else {
                    Log.e(TAG, "❌ 登录失败: ${response.errorMessage}")
                    AuthState.Error(response.errorMessage ?: "登录失败")
                }
            }
        )
    }

    /**
     * 用户注册
     * @param request 注册请求
     */
    suspend fun register(request: RegisterRequest): AuthState {
        Log.d(TAG, "========== 开始注册 ==========")
        Log.d(TAG, "手机号: ${request.phone}")
        Log.d(TAG, "邮箱: ${request.email}")

        return handleApiCall(
            operationName = "注册",
            request = request,
            apiCall = { authApiService.register(it) },
            successAction = { response ->
                if (response.success && response.user != null) {
                    val userEntity = response.user.toEntity()
                    userDao.insertUser(userEntity)
                    val loginTime = Clock.System.now().toString()
                    userDao.setLoggedIn(userEntity.id, loginTime)
                    saveToken(response.token, response.refreshToken)
                    Log.d(TAG, "✓ 注册成功")
                    Log.d(TAG, "用户ID: ${response.user.id}")
                    Log.d(TAG, "========== 注册完成 ==========")
                    AuthState.LoggedIn(response.user)
                } else {
                    Log.e(TAG, "❌ 注册失败: ${response.errorMessage}")
                    AuthState.Error(response.errorMessage ?: "注册失败")
                }
            }
        )
    }

    /**
     * 用户登出
     */
    suspend fun logout(): Boolean {
        Log.d(TAG, "========== 开始登出 ==========")

        return try {
            // 获取当前 Token
            val token = getToken()
            Log.d(TAG, "当前Token: ${token?.take(10)}...")

            // 调用后端登出 API
            if (token != null) {
                val request = com.example.babyfood.domain.model.LogoutRequest(token = token)
                val response = authApiService.logout(request)

                if (response.success) {
                    Log.d(TAG, "✓ 后端登出成功")
                } else {
                    Log.w(TAG, "⚠️ 后端登出失败: ${response.errorMessage}")
                }
            } else {
                Log.w(TAG, "⚠️ 未找到 Token，仅清除本地状态")
            }

            // 清除所有用户的登录状态
            userDao.logoutAll()

            // 清除Token
            clearToken()

            Log.d(TAG, "✓ 登出成功")
            Log.d(TAG, "========== 登出完成 ==========")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ 登出异常: ${e.message}")
            Log.e(TAG, "异常堆栈: ", e)

            // 即使 API 调用失败，也清除本地状态
            try {
                userDao.logoutAll()
                clearToken()
            } catch (clearException: Exception) {
                Log.e(TAG, "❌ 清除本地状态失败: ${clearException.message}")
            }

            false
        }
    }

    /**
     * 刷新Token
     */
    suspend fun refreshToken(): AuthState {
        Log.d(TAG, "========== 开始刷新Token ==========")

        val refreshToken = tokenStorage.getRefreshToken()
        if (refreshToken == null) {
            Log.e(TAG, "❌ 未找到刷新令牌")
            return AuthState.Error("未找到刷新令牌")
        }

        return handleApiCall(
            operationName = "Token刷新",
            request = refreshToken,
            apiCall = { authApiService.refreshToken(it) },
            successAction = { response ->
                if (response.success && response.user != null) {
                    tokenStorage.saveToken(
                        token = response.token ?: "",
                        refreshToken = response.refreshToken ?: "",
                        expiresIn = response.expiresIn ?: 7200,
                        userId = response.user.id
                    )
                    Log.d(TAG, "✓ Token刷新成功")
                    AuthState.LoggedIn(response.user)
                } else {
                    Log.e(TAG, "❌ Token刷新失败: ${response.errorMessage}")
                    AuthState.Error(response.errorMessage ?: "Token刷新失败")
                }
            }
        )
    }

    /**
     * 发送短信验证码
     * @param phone 手机号
     * @return Flow<Result<Boolean>> 发送结果
     */
    fun sendSmsVerificationCode(phone: String): Flow<Result<Boolean>> = flow {
        android.util.Log.d(TAG, "========== 发送短信验证码开始 ==========")
        android.util.Log.d(TAG, "手机号: $phone")

        try {
            val response = authApiService.sendSmsVerificationCode(phone)
            if (response.success) {
                android.util.Log.d(TAG, "✓ 验证码发送成功")
                emit(Result.success(true))
            } else {
                android.util.Log.e(TAG, "❌ 验证码发送失败: ${response.errorMessage}")
                emit(Result.failure(Exception(response.errorMessage ?: "发送失败")))
            }
            android.util.Log.d(TAG, "========== 发送短信验证码结束 ==========")
        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ 网络请求失败: ${e.message}", e)
            emit(Result.failure(e))
        }
    }

    /**
     * 发送邮件验证码
     * @param email 邮箱地址
     * @return Flow<Result<Boolean>> 发送结果
     */
    fun sendEmailVerificationCode(email: String): Flow<Result<Boolean>> = flow {
        android.util.Log.d(TAG, "========== 发送邮件验证码开始 ==========")
        android.util.Log.d(TAG, "邮箱: $email")

        try {
            val response = authApiService.sendEmailVerificationCode(email)
            if (response.success) {
                android.util.Log.d(TAG, "✓ 验证码发送成功")
                emit(Result.success(true))
            } else {
                android.util.Log.e(TAG, "❌ 验证码发送失败: ${response.errorMessage}")
                emit(Result.failure(Exception(response.errorMessage ?: "发送失败")))
            }
            android.util.Log.d(TAG, "========== 发送邮件验证码结束 ==========")
        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ 网络请求失败: ${e.message}", e)
            emit(Result.failure(e))
        }
    }

    /**
     * 用户注册（新实现）
     * @param phone 手机号（可选）
     * @param email 邮箱（可选）
     * @param password 密码
     * @param confirmPassword 确认密码
     * @param verificationCode 验证码
     * @return Flow<Result<User>> 注册结果
     */
    suspend fun register(
        phone: String?,
        email: String?,
        password: String,
        confirmPassword: String,
        verificationCode: String
    ): Flow<Result<User>> = flow {
        android.util.Log.d(TAG, "========== 用户注册开始 ==========")

        try {
            val request = RegisterRequest(
                phone = phone,
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                verificationCode = verificationCode,
                agreeToTerms = true,
                deviceInfo = com.example.babyfood.data.remote.dto.DeviceInfo(
                    platform = "android",
                    device_id = getDeviceId(),
                    app_version = "1.0.0"
                )
            )

            val response = authApiService.register(request)

            if (response.success && response.token != null && response.user != null) {
                android.util.Log.d(TAG, "✓ 注册成功，保存 Token")

                // 保存 Token
                response.expiresIn?.let {
                    tokenStorage.saveToken(
                        token = response.token,
                        refreshToken = response.refreshToken ?: "",
                        expiresIn = it,
                        userId = response.user.id
                    )
                }

                // 保存用户到数据库
                val userEntity = response.user.toEntity()
                userDao.insertUser(userEntity)
                val loginTime = Clock.System.now().toString()
                userDao.setLoggedIn(userEntity.id, loginTime)

                emit(Result.success(response.user))
            } else {
                android.util.Log.e(TAG, "❌ 注册失败: ${response.errorMessage}")
                emit(Result.failure(Exception(response.errorMessage ?: "注册失败")))
            }
            android.util.Log.d(TAG, "========== 用户注册结束 ==========")
        } catch (e: HttpException) {
            // 尝试解析 HTTP 错误响应
            val errorMessage = parseHttpErrorMessage(e, "注册")
            android.util.Log.e(TAG, "❌ 注册失败: $errorMessage")
            emit(Result.failure(Exception(errorMessage)))
        } catch (e: SocketTimeoutException) {
            android.util.Log.e(TAG, "❌ 连接超时: ${e.message}")
            emit(Result.failure(Exception("请求超时，请检查网络后重试")))
        } catch (e: IOException) {
            android.util.Log.e(TAG, "❌ 网络错误: ${e.message}")
            emit(Result.failure(Exception("网络错误，请检查网络连接")))
        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ 网络请求失败: ${e.message}", e)
            emit(Result.failure(e))
        }
    }

    /**
     * 检查是否已登录
     */
    suspend fun isLoggedIn(): Boolean {
        val user = userDao.getCurrentUser()
        return user != null
    }

    /**
     * 验证账号格式
     * @param account 手机号或邮箱
     * @return 是否为有效格式
     */
    fun validateAccount(account: String): Boolean {
        return PHONE_REGEX.matches(account) || EMAIL_REGEX.matches(account)
    }

    /**
     * 验证密码格式
     * @param password 密码
     * @return 是否为有效格式
     */
    fun validatePassword(password: String): Boolean {
        // 密码至少6位
        if (password.length < 6) return false
        
        // 密码不能超过72字节（bcrypt算法限制）
        // 注意：UTF-8编码下，中文字符占3字节，英文字符占1字节
        if (password.toByteArray(Charsets.UTF_8).size > 72) return false
        
        return true
    }

    // ========== 私有方法 ==========

    /**
     * 根据错误代码获取友好的错误提示
     * @param errorCode 错误代码
     * @param errorMessage 后端返回的错误消息
     * @return 友好的错误提示
     */
    private fun getFriendlyErrorMessage(errorCode: String?, errorMessage: String?): String {
        return when (errorCode) {
            "4011" -> "账号或密码错误，请检查后重试"
            "4012" -> "账号已被锁定，请30分钟后再试"
            "4001" -> "账号格式不正确"
            "4002" -> "密码格式不正确（密码长度需在6-72字节之间）"
            else -> errorMessage ?: "登录失败，请稍后重试"
        }
    }

    /**
     * 统一的API调用处理
     * @param operationName 操作名称（用于日志）
     * @param request API请求
     * @param apiCall API调用函数
     * @param successAction 成功回调
     * @return 认证状态
     */
    private suspend fun <T, R> handleApiCall(
        operationName: String,
        request: T,
        apiCall: suspend (T) -> R,
        successAction: suspend (R) -> AuthState
    ): AuthState {
        return try {
            val response = apiCall(request)
            successAction(response)
        } catch (e: HttpException) {
            handleHttpException(e, operationName)
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "❌ 连接超时: ${e.message}")
            AuthState.Error("请求超时，请检查网络后重试")
        } catch (e: IOException) {
            Log.e(TAG, "❌ 网络错误: ${e.message}")
            AuthState.Error("网络错误，请检查网络连接")
        } catch (e: Exception) {
            Log.e(TAG, "❌ 未知错误: ${e.message}")
            Log.e(TAG, "异常堆栈: ", e)
            AuthState.Error("${operationName}失败，请稍后重试")
        }
    }

    /**
     * 处理HTTP异常
     * @param e HTTP异常
     * @param operationName 操作名称
     * @return 认证状态
     */
    private fun handleHttpException(e: HttpException, operationName: String): AuthState {
        Log.e(TAG, "❌ HTTP错误: ${e.code()} - ${e.message()}")
        Log.e(TAG, "异常堆栈: ", e)

        val errorBody = e.response()?.errorBody()?.string()
        if (errorBody != null) {
            try {
                val errorResponse = Json.decodeFromString<LoginResponse>(errorBody)
                val errorMessage = getFriendlyErrorMessage(errorResponse.errorCode, errorResponse.errorMessage)
                Log.e(TAG, "❌ ${operationName}失败: $errorMessage (errorCode: ${errorResponse.errorCode})")
                return AuthState.Error(errorMessage)
            } catch (parseException: Exception) {
                Log.e(TAG, "❌ 解析错误响应失败: ${parseException.message}")
            }
        }
        return AuthState.Error("${operationName}失败，请稍后重试")
    }

    /**
     * 解析 HTTP 错误响应中的错误消息
     * @param e HTTP 异常
     * @param operationName 操作名称
     * @return 错误消息
     */
    private fun parseHttpErrorMessage(e: HttpException, operationName: String): String {
        Log.e(TAG, "❌ HTTP错误: ${e.code()} - ${e.message()}")
        Log.e(TAG, "异常堆栈: ", e)

        val errorBody = e.response()?.errorBody()?.string()
        if (errorBody != null) {
            try {
                // 尝试解析为 RegisterResponse
                val errorResponse = Json.decodeFromString<RegisterResponse>(errorBody)
                if (!errorResponse.errorMessage.isNullOrBlank()) {
                    // 翻译错误消息
                    return translateErrorMessage(errorResponse.errorMessage)
                }
            } catch (parseException: Exception) {
                Log.e(TAG, "❌ 解析 RegisterResponse 失败: ${parseException.message}")
            }

            try {
                // 尝试解析为 LoginResponse
                val errorResponse = Json.decodeFromString<LoginResponse>(errorBody)
                if (!errorResponse.errorMessage.isNullOrBlank()) {
                    // 翻译错误消息
                    return translateErrorMessage(errorResponse.errorMessage)
                }
            } catch (parseException: Exception) {
                Log.e(TAG, "❌ 解析 LoginResponse 失败: ${parseException.message}")
            }
        }

        // 根据状态码返回默认错误消息
        return when (e.code()) {
            409 -> "账号已被注册，请直接登录"
            400 -> "请求参数错误，请检查输入"
            401 -> "认证失败，请重新登录"
            403 -> "权限不足"
            404 -> "资源不存在"
            429 -> "请求过于频繁，请稍后再试"
            500 -> "服务器错误，请稍后重试"
            else -> "${operationName}失败，请稍后重试"
        }
    }

    /**
     * 翻译错误消息
     * @param errorMessage 原始错误消息
     * @return 翻译后的错误消息
     */
    private fun translateErrorMessage(errorMessage: String): String {
        return when (errorMessage) {
            // 注册相关错误
            "Phone number already registered." -> "手机号已被注册，请使用其他手机号或直接登录"
            "Email address already registered." -> "邮箱已被注册，请使用其他邮箱或直接登录"
            "Invalid phone number format." -> "手机号格式不正确"
            "Invalid email format." -> "邮箱格式不正确"
            "Password is required." -> "密码不能为空"
            "Password must be at least 6 characters." -> "密码至少需要6个字符"
            "Passwords do not match." -> "两次输入的密码不一致"
            "Verification code is required." -> "验证码不能为空"
            "Invalid verification code." -> "验证码无效"
            "Verification code has expired." -> "验证码已过期，请重新获取"
            "You must agree to the terms and privacy policy." -> "请同意服务条款和隐私政策"

            // 登录相关错误
            "Account not found." -> "账号不存在"
            "Incorrect password." -> "密码错误"
            "Account is locked." -> "账号已被锁定，请30分钟后再试"
            "Account is disabled." -> "账号已被禁用"
            "Invalid credentials." -> "账号或密码错误，请检查后重试"

            // 验证码相关错误
            "Verification code not found." -> "验证码不存在或已过期"
            "Too many verification code requests." -> "验证码请求过于频繁，请稍后再试"

            // 通用错误
            "Internal server error." -> "服务器内部错误，请稍后重试"
            "Service unavailable." -> "服务暂时不可用，请稍后重试"
            else -> errorMessage // 如果没有对应的翻译，返回原始消息
        }
    }

    private fun saveToken(token: String?, refreshToken: String?) {
        val userId = tokenStorage.getUserId()
        tokenStorage.saveToken(
            token = token ?: "",
            refreshToken = refreshToken ?: "",
            expiresIn = 7200, // 2小时
            userId = userId
        )
    }

    private fun clearToken() {
        tokenStorage.clear()
    }

    private fun getToken(): String? {
        return tokenStorage.getToken()
    }

    private fun getRefreshToken(): String? {
        return tokenStorage.getRefreshToken()
    }

    /**
     * 获取设备唯一标识
     * @return 设备 ID
     */
    private fun getDeviceId(): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: UUID.randomUUID().toString()
    }

    private fun User.toEntity(): UserEntity = UserEntity(
        id = id,
        phone = phone,
        email = email,
        nickname = nickname,
        avatar = avatar,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString(),
        isEmailVerified = isEmailVerified,
        isPhoneVerified = isPhoneVerified,
        isLoggedIn = false,
        lastLoginTime = null
    )

    private fun UserEntity.toDomainModel(): User = User(
        id = id,
        username = "", // 数据库中没有存储 username
        phone = phone,
        email = email,
        nickname = nickname,
        avatar = avatar,
        createdAt = createdAt.toLongOrNull() ?: 0L,
        updatedAt = updatedAt.toLongOrNull() ?: 0L,
        isEmailVerified = isEmailVerified,
        isPhoneVerified = isPhoneVerified,
        role = "user"
    )
}