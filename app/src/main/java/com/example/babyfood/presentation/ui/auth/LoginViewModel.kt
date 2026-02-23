package com.example.babyfood.presentation.ui.auth

import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.repository.AuthRepository
import com.example.babyfood.domain.model.AuthState
import com.example.babyfood.presentation.ui.BaseViewModel
import com.example.babyfood.util.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

/**
 * 登录页面 ViewModel
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    override val logTag: String = "LoginViewModel"

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.NotLoggedIn)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        logMethodStart("初始化 LoginViewModel")
        logD("初始表单状态: 账号='${_uiState.value.account}', 密码长度=${_uiState.value.password.length}, 表单有效=${_uiState.value.isFormValid}")
        checkLoginStatus()
    }

    /**
     * 检查登录状态
     */
    private fun checkLoginStatus() {
        viewModelScope.launch {
            val isLoggedIn = authRepository.isLoggedIn()
            logD("当前登录状态: $isLoggedIn")
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
        logD("账号输入: '$account'")
        _uiState.value = _uiState.value.copy(account = account)
        validateForm()
    }

    /**
     * 更新密码输入
     */
    fun onPasswordChange(password: String) {
        logD("密码输入: 长度=${password.length}")
        _uiState.value = _uiState.value.copy(password = password)
        validateForm()
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
     * 执行登录
     */
    fun login() {
        logMethodStart("登录流程")
        val account = _uiState.value.account.trim()
        val password = _uiState.value.password
        val rememberMe = _uiState.value.rememberMe

        logD("当前UI状态: 账号='$account', 密码长度=${password.length}, 记住我=$rememberMe, 表单有效=${_uiState.value.isFormValid}, 加载中=${_uiState.value.isLoading}")

        // 表单验证
        val validationError = validateLoginInput(account, password)
        if (validationError != null) {
            logError("表单验证失败: $validationError")
            return
        }

        // 开始登录
        logSuccess("表单验证通过，开始登录请求")
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            accountError = null,
            passwordError = null
        )

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(account, password, rememberMe)
            handleLoginResult(result)
        }
    }

    /**
     * 验证登录输入
     * @return 错误信息，验证通过返回 null
     */
    private fun validateLoginInput(account: String, password: String): String? {
        if (account.isEmpty()) {
            _uiState.value = _uiState.value.copy(accountError = "请输入手机号或邮箱")
            return "账号为空"
        }

        val isAccountValid = authRepository.validateAccount(account)
        logD("账号验证结果: $isAccountValid")

        if (!isAccountValid) {
            _uiState.value = _uiState.value.copy(accountError = "请输入有效的手机号或邮箱")
            return "账号格式无效"
        }

        if (password.isEmpty()) {
            _uiState.value = _uiState.value.copy(passwordError = "请输入密码")
            return "密码为空"
        }

        val isPasswordValid = authRepository.validatePassword(password)
        logD("密码验证结果: $isPasswordValid (字符长度: ${password.length})")

        if (!isPasswordValid) {
            _uiState.value = _uiState.value.copy(
                passwordError = if (password.length < ValidationUtils.MIN_PASSWORD_LENGTH)
                    "密码至少${ValidationUtils.MIN_PASSWORD_LENGTH}位"
                else
                    "密码过长，请缩短密码（不超过${ValidationUtils.MAX_PASSWORD_BYTES}字节）"
            )
            return "密码格式无效"
        }

        return null
    }

    /**
     * 处理登录结果
     */
    private fun handleLoginResult(result: AuthState) {
        when (result) {
            is AuthState.LoggedIn -> {
                logSuccess("登录成功，导航到首页")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoginSuccess = true
                )
                _authState.value = result
            }
            is AuthState.Error -> {
                logError("登录失败: ${result.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.message
                )
                _authState.value = result
            }
            else -> {
                logError("未知的登录状态: $result")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "登录失败，请重试"
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
        logMethodStart("取消登录请求")
        _uiState.value = _uiState.value.copy(isLoading = false)
        logMethodEnd("取消登录请求")
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

        logD("表单验证: 账号非空=$isAccountNotEmpty, 密码非空=$isPasswordNotEmpty, 账号有效=$isAccountValid, 密码有效=$isPasswordValid, 表单有效=$isValid")

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
