package com.example.babyfood.presentation.ui.auth

import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.repository.AuthRepository
import com.example.babyfood.domain.model.AuthState
import com.example.babyfood.presentation.ui.BaseViewModel
import com.example.babyfood.util.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 注册页面 ViewModel
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    override val logTag: String = "RegisterViewModel"

    companion object {
        private const val COUNTDOWN_TIME = 60 // 倒计时时间（秒）
    }

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.NotLoggedIn)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        logMethodStart("初始化 RegisterViewModel")
    }

    /**
     * 更新账号输入（手机号或邮箱）
     */
    fun onAccountChange(account: String) {
        _uiState.value = _uiState.value.copy(account = account)
        validateForm()
    }

    /**
     * 更新验证码输入
     */
    fun onVerificationCodeChange(code: String) {
        _uiState.value = _uiState.value.copy(verificationCode = code)
        validateForm()
    }

    /**
     * 更新密码输入
     */
    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
        validateForm()
    }

    /**
     * 更新确认密码输入
     */
    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword)
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
     * 切换确认密码可见性
     */
    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible
        )
    }

    /**
     * 切换"同意服务条款"状态
     */
    fun toggleAgreeToTerms() {
        _uiState.value = _uiState.value.copy(
            agreeToTerms = !_uiState.value.agreeToTerms,
            agreeToTermsError = null
        )
        validateForm()
    }

    /**
     * 发送验证码
     */
    fun sendVerificationCode() {
        logMethodStart("发送验证码")
        val account = _uiState.value.account.trim()

        // 验证账号
        val accountValidation = validateAccount(account)
        if (accountValidation != null) {
            _uiState.value = _uiState.value.copy(accountError = accountValidation)
            return
        }

        // 判断账号类型
        val isPhone = !account.contains("@")
        val accountType = if (isPhone) "手机号" else "邮箱"

        // 开始发送
        _uiState.value = _uiState.value.copy(
            isSendingCode = true,
            accountError = null
        )

        logD("发送验证码到 $accountType: $account")

        viewModelScope.launch {
            val flow = if (isPhone) {
                authRepository.sendSmsVerificationCode(account)
            } else {
                authRepository.sendEmailVerificationCode(account)
            }

            flow.collect { result ->
                result.onSuccess {
                    logSuccess("验证码发送成功到 $accountType: $account")
                    // 开始倒计时
                    startCountdown()
                    _uiState.value = _uiState.value.copy(
                        isSendingCode = false,
                        error = null
                    )
                }.onFailure { exception ->
                    logError("验证码发送失败: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isSendingCode = false,
                        error = exception.message ?: "验证码发送失败，请重试"
                    )
                }
            }
        }
    }

    /**
     * 验证账号
     * @return 错误信息，验证通过返回 null
     */
    private fun validateAccount(account: String): String? {
        return when {
            account.isEmpty() -> "请输入手机号或邮箱"
            !authRepository.validateAccount(account) -> "请输入有效的手机号或邮箱"
            else -> null
        }
    }

    /**
     * 开始倒计时
     */
    private fun startCountdown() {
        countdownJob?.cancel()
        _uiState.value = _uiState.value.copy(countdown = COUNTDOWN_TIME)

        countdownJob = viewModelScope.launch {
            for (i in COUNTDOWN_TIME downTo 1) {
                delay(1000)
                _uiState.value = _uiState.value.copy(countdown = i)
            }
            _uiState.value = _uiState.value.copy(countdown = 0)
        }
    }

    /**
     * 注册
     */
    fun register() {
        logMethodStart("注册流程")

        // 验证表单
        val validationError = validateRegisterForm()
        if (validationError != null) {
            logError("表单验证失败: $validationError")
            return
        }

        val account = _uiState.value.account.trim()
        val password = _uiState.value.password
        val verificationCode = _uiState.value.verificationCode.trim()

        // 判断账号类型
        val isPhone = !account.contains("@")
        val phone = if (isPhone) account else null
        val email = if (!isPhone) account else null

        logD("注册信息: 类型=${if (isPhone) "手机" else "邮箱"}, 账号=$account")

        // 开始注册
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            accountError = null,
            verificationCodeError = null,
            passwordError = null,
            confirmPasswordError = null,
            agreeToTermsError = null
        )

        viewModelScope.launch {
            _authState.value = AuthState.Loading

            authRepository.register(
                phone = phone,
                email = email,
                password = password,
                confirmPassword = _uiState.value.confirmPassword,
                verificationCode = verificationCode
            ).collect { result ->
                result.onSuccess { user ->
                    logSuccess("注册成功，导航到首页")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegisterSuccess = true
                    )
                    _authState.value = AuthState.LoggedIn(user)
                }.onFailure { exception ->
                    logError("注册失败: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "注册失败，请重试"
                    )
                    _authState.value = AuthState.Error(exception.message ?: "注册失败，请重试")
                }
            }
        }
    }

    /**
     * 验证注册表单
     * @return 错误信息，验证通过返回 null
     */
    private fun validateRegisterForm(): String? {
        val state = _uiState.value
        val account = state.account.trim()
        val password = state.password
        val confirmPassword = state.confirmPassword
        val verificationCode = state.verificationCode.trim()
        val agreeToTerms = state.agreeToTerms

        // 验证账号
        if (account.isEmpty()) {
            _uiState.value = state.copy(accountError = "请输入手机号或邮箱")
            return "账号为空"
        }
        if (!authRepository.validateAccount(account)) {
            _uiState.value = state.copy(accountError = "请输入有效的手机号或邮箱")
            return "账号格式无效"
        }

        // 验证验证码
        if (verificationCode.isEmpty()) {
            _uiState.value = state.copy(verificationCodeError = "请输入验证码")
            return "验证码为空"
        }
        if (verificationCode.length != ValidationUtils.VERIFICATION_CODE_LENGTH) {
            _uiState.value = state.copy(verificationCodeError = "验证码格式错误")
            return "验证码格式错误"
        }

        // 验证密码
        if (password.isEmpty()) {
            _uiState.value = state.copy(passwordError = "请输入密码")
            return "密码为空"
        }
        if (!authRepository.validatePassword(password)) {
            _uiState.value = state.copy(
                passwordError = if (password.length < ValidationUtils.MIN_PASSWORD_LENGTH)
                    "密码至少${ValidationUtils.MIN_PASSWORD_LENGTH}位"
                else
                    "密码过长，请缩短密码（不超过${ValidationUtils.MAX_PASSWORD_BYTES}字节）"
            )
            return "密码格式无效"
        }

        // 验证确认密码
        if (confirmPassword.isEmpty()) {
            _uiState.value = state.copy(confirmPasswordError = "请确认密码")
            return "确认密码为空"
        }
        if (password != confirmPassword) {
            _uiState.value = state.copy(confirmPasswordError = "两次输入的密码不一致")
            return "密码不匹配"
        }

        // 验证同意条款
        if (!agreeToTerms) {
            _uiState.value = state.copy(agreeToTermsError = "请同意服务条款和隐私政策")
            return "未同意条款"
        }

        return null
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
     * 清除验证码错误
     */
    fun clearVerificationCodeError() {
        _uiState.value = _uiState.value.copy(verificationCodeError = null)
    }

    /**
     * 清除密码错误
     */
    fun clearPasswordError() {
        _uiState.value = _uiState.value.copy(passwordError = null)
    }

    /**
     * 清除确认密码错误
     */
    fun clearConfirmPasswordError() {
        _uiState.value = _uiState.value.copy(confirmPasswordError = null)
    }

    /**
     * 清除注册成功标志
     */
    fun clearRegisterSuccessFlag() {
        _uiState.value = _uiState.value.copy(isRegisterSuccess = false)
    }

    /**
     * 表单验证
     */
    private fun validateForm() {
        val account = _uiState.value.account.trim()
        val password = _uiState.value.password
        val confirmPassword = _uiState.value.confirmPassword
        val verificationCode = _uiState.value.verificationCode.trim()
        val agreeToTerms = _uiState.value.agreeToTerms

        val isAccountValid = account.isNotEmpty() && authRepository.validateAccount(account)
        val isPasswordValid = password.isNotEmpty() && authRepository.validatePassword(password)
        val isConfirmPasswordValid = confirmPassword.isNotEmpty() && password == confirmPassword
        val isVerificationCodeValid = verificationCode.isNotEmpty() &&
            verificationCode.length == ValidationUtils.VERIFICATION_CODE_LENGTH

        _uiState.value = _uiState.value.copy(
            isFormValid = isAccountValid &&
                isPasswordValid &&
                isConfirmPasswordValid &&
                isVerificationCodeValid &&
                agreeToTerms
        )
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}

/**
 * 注册页面 UI 状态
 */
data class RegisterUiState(
    val account: String = "",
    val verificationCode: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val agreeToTerms: Boolean = false,
    val isSendingCode: Boolean = false,
    val countdown: Int = 0,
    val isLoading: Boolean = false,
    val isRegisterSuccess: Boolean = false,
    val isFormValid: Boolean = false,
    val accountError: String? = null,
    val verificationCodeError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val agreeToTermsError: String? = null,
    val error: String? = null
)
