package com.example.babyfood.presentation.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.repository.AuthRepository
import com.example.babyfood.domain.model.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 登录页面 ViewModel
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
        private val PHONE_REGEX = Regex("^1[3-9]\\d{9}$")
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.NotLoggedIn)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        Log.d(TAG, "========== 初始化 LoginViewModel ==========")
        Log.d(TAG, "初始表单状态:")
        Log.d(TAG, "  账号: '${_uiState.value.account}'")
        Log.d(TAG, "  密码长度: ${_uiState.value.password.length}")
        Log.d(TAG, "  表单有效: ${_uiState.value.isFormValid}")
        checkLoginStatus()
    }

    /**
     * 检查登录状态
     */
    private fun checkLoginStatus() {
        viewModelScope.launch {
            val isLoggedIn = authRepository.isLoggedIn()
            Log.d(TAG, "当前登录状态: $isLoggedIn")
            if (isLoggedIn) {
                authRepository.getCurrentUser().collect { user ->
                    if (user != null) {
                        _authState.value = AuthState.LoggedIn(user)
                    }
                }
            }
        }
    }

    /**
     * 更新账号输入
     */
    fun onAccountChange(account: String) {
        Log.d(TAG, "账号输入: '$account'")
        _uiState.value = _uiState.value.copy(account = account)
        validateForm()
        Log.d(TAG, "账号输入后状态: account='${_uiState.value.account}'")
    }

    /**
     * 更新密码输入
     */
    fun onPasswordChange(password: String) {
        Log.d(TAG, "密码输入: 长度=${password.length}")
        _uiState.value = _uiState.value.copy(password = password)
        validateForm()
        Log.d(TAG, "密码输入后状态: passwordLength=${_uiState.value.password.length}")
    }

    /**
     * 切换密码可见性
     */
    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }

    /**
     * 切换"记住我"状态
     */
    fun toggleRememberMe() {
        _uiState.value = _uiState.value.copy(
            rememberMe = !_uiState.value.rememberMe
        )
    }

    /**
     * 登录
     */
    fun login() {
        Log.d(TAG, "========== 开始登录流程 ==========")
        val account = _uiState.value.account.trim()
        val password = _uiState.value.password
        val rememberMe = _uiState.value.rememberMe

        Log.d(TAG, "当前UI状态:")
        Log.d(TAG, "  账号: '$account'")
        Log.d(TAG, "  密码长度: ${password.length}")
        Log.d(TAG, "  记住我: $rememberMe")
        Log.d(TAG, "  表单有效: ${_uiState.value.isFormValid}")
        Log.d(TAG, "  加载中: ${_uiState.value.isLoading}")

        // 表单验证
        if (account.isEmpty()) {
            Log.e(TAG, "❌ 账号为空")
            _uiState.value = _uiState.value.copy(
                accountError = "请输入手机号或邮箱"
            )
            return
        }

        val isAccountValid = authRepository.validateAccount(account)
        Log.d(TAG, "账号验证结果: $isAccountValid (手机号格式: ${PHONE_REGEX.matches(account)}, 邮箱格式: ${EMAIL_REGEX.matches(account)})")

        if (!isAccountValid) {
            Log.e(TAG, "❌ 账号格式无效")
            _uiState.value = _uiState.value.copy(
                accountError = "请输入有效的手机号或邮箱"
            )
            return
        }

        if (password.isEmpty()) {
            Log.e(TAG, "❌ 密码为空")
            _uiState.value = _uiState.value.copy(
                passwordError = "请输入密码"
            )
            return
        }

        val isPasswordValid = authRepository.validatePassword(password)
        val passwordBytes = password.toByteArray(Charsets.UTF_8).size
        Log.d(TAG, "密码验证结果: $isPasswordValid (字符长度: ${password.length}, 字节长度: $passwordBytes, 要求: 6-72字节)")

        if (!isPasswordValid) {
            Log.e(TAG, "❌ 密码格式无效")
            _uiState.value = _uiState.value.copy(
                passwordError = if (password.length < 6) "密码至少6位" else "密码过长，请缩短密码（不超过72字节）"
            )
            return
        }

        // 开始登录
        Log.d(TAG, "✓ 表单验证通过，开始登录请求")
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            accountError = null,
            passwordError = null
        )

        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val result = authRepository.login(account, password, rememberMe)

                when (result) {
                    is AuthState.LoggedIn -> {
                        Log.d(TAG, "✓ 登录成功，导航到首页")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoginSuccess = true
                        )
                        _authState.value = result
                    }
                    is AuthState.Error -> {
                        Log.e(TAG, "❌ 登录失败: ${result.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                        _authState.value = result
                    }
                    else -> {
                        Log.e(TAG, "❌ 未知的登录状态: $result")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "登录失败，请重试"
                        )
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                Log.e(TAG, "❌ 连接超时: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "请求超时，请检查网络后重试"
                )
            } catch (e: java.util.concurrent.TimeoutException) {
                Log.e(TAG, "❌ 读取超时: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "请求超时，请检查网络后重试"
                )
            } catch (e: java.io.IOException) {
                Log.e(TAG, "❌ 网络错误: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "网络错误，请检查网络连接"
                )
            } catch (e: Exception) {
                Log.e(TAG, "❌ 未知错误: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "登录失败，请稍后重试"
                )
            }
        }
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * 清除账号错误
     */
    fun clearAccountError() {
        _uiState.value = _uiState.value.copy(accountError = null)
    }

    /**
     * 清除密码错误
     */
    fun clearPasswordError() {
        _uiState.value = _uiState.value.copy(passwordError = null)
    }

    /**
     * 清除登录成功标志
     */
    fun clearLoginSuccessFlag() {
        _uiState.value = _uiState.value.copy(isLoginSuccess = false)
    }

    /**
     * 取消登录请求
     */
    fun cancelLogin() {
        Log.d(TAG, "========== 取消登录请求 ==========")
        _uiState.value = _uiState.value.copy(isLoading = false)
        // TODO: 如果需要，可以在这里取消实际的协程任务
    }

    /**
     * 表单验证
     */
    private fun validateForm() {
        val account = _uiState.value.account.trim()
        val password = _uiState.value.password

        val isAccountNotEmpty = account.isNotEmpty()
        val isPasswordNotEmpty = password.isNotEmpty()
        val isAccountValid = authRepository.validateAccount(account)
        val isPasswordValid = authRepository.validatePassword(password)

        val isValid = isAccountNotEmpty && isPasswordNotEmpty && isAccountValid && isPasswordValid

        Log.d(TAG, "表单验证:")
        Log.d(TAG, "  账号非空: $isAccountNotEmpty")
        Log.d(TAG, "  密码非空: $isPasswordNotEmpty")
        Log.d(TAG, "  账号有效: $isAccountValid")
        Log.d(TAG, "  密码有效: $isPasswordValid")
        Log.d(TAG, "  表单有效: $isValid")

        _uiState.value = _uiState.value.copy(isFormValid = isValid)
    }
}

/**
 * 登录页面 UI 状态
 */
data class LoginUiState(
    val account: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val isFormValid: Boolean = false,
    val accountError: String? = null,
    val passwordError: String? = null,
    val error: String? = null
)