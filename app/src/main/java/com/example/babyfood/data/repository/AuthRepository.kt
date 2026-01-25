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

        return try {
            // 调用API登录
            val request = LoginRequest(
                account = account,
                password = password,
                rememberMe = rememberMe
            )
            val response = authApiService.login(request)

            if (response.success && response.user != null) {
                Log.d(TAG, "✓ 登录成功")
                Log.d(TAG, "用户ID: ${response.user.id}")
                Log.d(TAG, "用户昵称: ${response.user.nickname}")

                // 保存用户信息到本地数据库
                val userEntity = response.user.toEntity()
                userDao.insertUser(userEntity)

                // 设置登录状态
                val loginTime = Clock.System.now().toString()
                userDao.setLoggedIn(userEntity.id, loginTime)

                // 保存Token到SharedPreferences（这里简化处理，实际应使用加密存储）
                saveToken(response.token, response.refreshToken)

                Log.d(TAG, "========== 登录完成 ==========")
                AuthState.LoggedIn(response.user)
            } else {
                Log.e(TAG, "❌ 登录失败: ${response.errorMessage}")
                AuthState.Error(response.errorMessage ?: "登录失败")
            }
        } catch (e: HttpException) {
            // 处理 HTTP 错误（4xx, 5xx）
            Log.e(TAG, "❌ HTTP错误: ${e.code()} - ${e.message()}")
            Log.e(TAG, "异常堆栈: ", e)

            // 尝试从响应体中提取错误信息
            val errorBody = e.response()?.errorBody()?.string()
            if (errorBody != null) {
                try {
                    val errorResponse = Json.decodeFromString<LoginResponse>(errorBody)
                    val errorMessage = getFriendlyErrorMessage(errorResponse.errorCode, errorResponse.errorMessage)
                    Log.e(TAG, "❌ 登录失败: $errorMessage (errorCode: ${errorResponse.errorCode})")
                    AuthState.Error(errorMessage)
                } catch (parseException: Exception) {
                    Log.e(TAG, "❌ 解析错误响应失败: ${parseException.message}")
                    AuthState.Error("登录失败，请稍后重试")
                }
            } else {
                AuthState.Error("登录失败，请稍后重试")
            }
        } catch (e: SocketTimeoutException) {
            // 处理连接超时
            Log.e(TAG, "❌ 连接超时: ${e.message}")
            AuthState.Error("请求超时，请检查网络后重试")
        } catch (e: IOException) {
            // 处理网络错误
            Log.e(TAG, "❌ 网络错误: ${e.message}")
            AuthState.Error("网络错误，请检查网络连接")
        } catch (e: Exception) {
            // 处理其他未知错误
            Log.e(TAG, "❌ 未知错误: ${e.message}")
            Log.e(TAG, "异常堆栈: ", e)
            AuthState.Error("登录失败，请稍后重试")
        }
    }

    /**
     * 用户注册
     * @param request 注册请求
     */
    suspend fun register(request: RegisterRequest): AuthState {
        Log.d(TAG, "========== 开始注册 ==========")
        Log.d(TAG, "手机号: ${request.phone}")
        Log.d(TAG, "邮箱: ${request.email}")

        return try {
            // 调用API注册
            val response = authApiService.register(request)

            if (response.success && response.user != null) {
                Log.d(TAG, "✓ 注册成功")
                Log.d(TAG, "用户ID: ${response.user.id}")

                // 保存用户信息到本地数据库
                val userEntity = response.user.toEntity()
                userDao.insertUser(userEntity)

                // 设置登录状态
                val loginTime = Clock.System.now().toString()
                userDao.setLoggedIn(userEntity.id, loginTime)

                // 保存Token
                saveToken(response.token, response.refreshToken)

                Log.d(TAG, "========== 注册完成 ==========")
                AuthState.LoggedIn(response.user)
            } else {
                Log.e(TAG, "❌ 注册失败: ${response.errorMessage}")
                AuthState.Error(response.errorMessage ?: "注册失败")
            }
        } catch (e: HttpException) {
            // 处理 HTTP 错误（4xx, 5xx）
            Log.e(TAG, "❌ HTTP错误: ${e.code()} - ${e.message()}")
            Log.e(TAG, "异常堆栈: ", e)

            // 尝试从响应体中提取错误信息
            val errorBody = e.response()?.errorBody()?.string()
            if (errorBody != null) {
                try {
                    val errorResponse = Json.decodeFromString<LoginResponse>(errorBody)
                    val errorMessage = errorResponse.errorMessage ?: "注册失败，请稍后重试"
                    Log.e(TAG, "❌ 注册失败: $errorMessage (errorCode: ${errorResponse.errorCode})")
                    AuthState.Error(errorMessage)
                } catch (parseException: Exception) {
                    Log.e(TAG, "❌ 解析错误响应失败: ${parseException.message}")
                    AuthState.Error("注册失败，请稍后重试")
                }
            } else {
                AuthState.Error("注册失败，请稍后重试")
            }
        } catch (e: SocketTimeoutException) {
            // 处理连接超时
            Log.e(TAG, "❌ 连接超时: ${e.message}")
            AuthState.Error("请求超时，请检查网络后重试")
        } catch (e: IOException) {
            // 处理网络错误
            Log.e(TAG, "❌ 网络错误: ${e.message}")
            AuthState.Error("网络错误，请检查网络连接")
        } catch (e: Exception) {
            // 处理其他未知错误
            Log.e(TAG, "❌ 未知错误: ${e.message}")
            Log.e(TAG, "异常堆栈: ", e)
            AuthState.Error("注册失败，请稍后重试")
        }
    }

    /**
     * 用户登出
     */
    suspend fun logout() {
        Log.d(TAG, "========== 开始登出 ==========")

        try {
            // 清除所有用户的登录状态
            userDao.logoutAll()

            // 清除Token
            clearToken()

            Log.d(TAG, "✓ 登出成功")
            Log.d(TAG, "========== 登出完成 ==========")
        } catch (e: Exception) {
            Log.e(TAG, "❌ 登出异常: ${e.message}")
            Log.e(TAG, "异常堆栈: ", e)
        }
    }

    /**
     * 刷新Token
     */
    suspend fun refreshToken(): AuthState {
        Log.d(TAG, "========== 开始刷新Token ==========")

        return try {
            val refreshToken = getRefreshToken()
            if (refreshToken == null) {
                Log.e(TAG, "❌ 未找到刷新令牌")
                return AuthState.Error("未找到刷新令牌")
            }

            val response = authApiService.refreshToken(refreshToken)

            if (response.success && response.user != null) {
                Log.d(TAG, "✓ Token刷新成功")

                // 保存新的Token
                saveToken(response.token, response.refreshToken)

                AuthState.LoggedIn(response.user)
            } else {
                Log.e(TAG, "❌ Token刷新失败: ${response.errorMessage}")
                AuthState.Error(response.errorMessage ?: "Token刷新失败")
            }
        } catch (e: HttpException) {
            // 处理 HTTP 错误（4xx, 5xx）
            Log.e(TAG, "❌ HTTP错误: ${e.code()} - ${e.message()}")
            Log.e(TAG, "异常堆栈: ", e)

            // 尝试从响应体中提取错误信息
            val errorBody = e.response()?.errorBody()?.string()
            if (errorBody != null) {
                try {
                    val errorResponse = Json.decodeFromString<LoginResponse>(errorBody)
                    val errorMessage = errorResponse.errorMessage ?: "Token刷新失败，请稍后重试"
                    Log.e(TAG, "❌ Token刷新失败: $errorMessage (errorCode: ${errorResponse.errorCode})")
                    AuthState.Error(errorMessage)
                } catch (parseException: Exception) {
                    Log.e(TAG, "❌ 解析错误响应失败: ${parseException.message}")
                    AuthState.Error("Token刷新失败，请稍后重试")
                }
            } else {
                AuthState.Error("Token刷新失败，请稍后重试")
            }
        } catch (e: SocketTimeoutException) {
            // 处理连接超时
            Log.e(TAG, "❌ 连接超时: ${e.message}")
            AuthState.Error("请求超时，请检查网络后重试")
        } catch (e: IOException) {
            // 处理网络错误
            Log.e(TAG, "❌ 网络错误: ${e.message}")
            AuthState.Error("网络错误，请检查网络连接")
        } catch (e: Exception) {
            // 处理其他未知错误
            Log.e(TAG, "❌ 未知错误: ${e.message}")
            Log.e(TAG, "异常堆栈: ", e)
            AuthState.Error("Token刷新失败，请稍后重试")
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
        val phoneRegex = Regex("^1[3-9]\\d{9}$")
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return phoneRegex.matches(account) || emailRegex.matches(account)
    }

    /**
     * 验证密码格式
     * @param password 密码
     * @return 是否为有效格式
     */
    fun validatePassword(password: String): Boolean {
        // 密码至少6位
        return password.length >= 6
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
            "4002" -> "密码格式不正确"
            else -> errorMessage ?: "登录失败，请稍后重试"
        }
    }

    private fun saveToken(token: String?, refreshToken: String?) {
        // TODO: 实现安全的Token存储（使用Android Keystore或EncryptedSharedPreferences）
        Log.d(TAG, "保存Token: ${token?.take(10)}...")
    }

    private fun clearToken() {
        // TODO: 清除存储的Token
        Log.d(TAG, "清除Token")
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