package com.example.babyfood.data.repository

import android.util.Log
import com.example.babyfood.data.local.database.dao.UserDao
import com.example.babyfood.data.local.database.entity.UserEntity
import com.example.babyfood.data.remote.api.AuthApiService
import com.example.babyfood.domain.model.AuthState
import com.example.babyfood.domain.model.LoginRequest
import com.example.babyfood.domain.model.LoginResponse
import com.example.babyfood.domain.model.RegisterRequest
import com.example.babyfood.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 认证仓库
 * 处理用户登录、注册、登出等认证相关操作
 */
@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val authApiService: AuthApiService
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

        val refreshToken = getRefreshToken()
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
                    saveToken(response.token, response.refreshToken)
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

    private fun saveToken(token: String?, refreshToken: String?) {
        // TODO: 实现安全的Token存储（使用Android Keystore或EncryptedSharedPreferences）
        Log.d(TAG, "保存Token: ${token?.take(10)}...")
    }

    private fun clearToken() {
        // TODO: 清除存储的Token
        Log.d(TAG, "清除Token")
    }

    private fun getToken(): String? {
        // TODO: 从安全存储获取访问令牌
        return null
    }

    private fun getRefreshToken(): String? {
        // TODO: 从安全存储获取刷新令牌
        return null
    }

    private fun User.toEntity(): UserEntity = UserEntity(
        id = id,
        phone = phone,
        email = email,
        nickname = nickname,
        avatar = avatar,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isEmailVerified = isEmailVerified,
        isPhoneVerified = isPhoneVerified,
        isLoggedIn = false,
        lastLoginTime = null
    )

    private fun UserEntity.toDomainModel(): User = User(
        id = id,
        phone = phone,
        email = email,
        nickname = nickname,
        avatar = avatar,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isEmailVerified = isEmailVerified,
        isPhoneVerified = isPhoneVerified
    )
}