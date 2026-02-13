package com.example.babyfood.presentation.ui.user

import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.repository.AuthRepository
import com.example.babyfood.domain.model.User
import com.example.babyfood.presentation.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 个人设置页面 ViewModel
 * 管理用户信息、主题设置等状态
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    override val logTag: String = "SettingsViewModel"

    // UI 状态
    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    // 操作状态
    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState.asStateFlow()

    init {
        logMethodStart("SettingsViewModel 初始化")
        loadUserInfo()
    }

    /**
     * 加载用户信息
     */
    fun loadUserInfo() {
        logMethodStart("加载用户信息")
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                if (user != null) {
                    _uiState.value = SettingsUiState.Success(user)
                    logSuccess("用户信息加载成功")
                    logD("用户昵称: ${user.nickname}, 主题: ${user.theme}")
                } else {
                    _uiState.value = SettingsUiState.NotLoggedIn
                    logWarning("用户未登录")
                }
                logMethodEnd("加载用户信息")
            }
        }
    }

    /**
     * 更新昵称
     * @param nickname 新昵称
     */
    fun updateNickname(nickname: String) {
        logMethodStart("更新昵称")
        logD("新昵称: $nickname")

        safeLaunch("更新昵称", onError = { _operationState.value = OperationState.Error(it.message ?: "更新失败") }) {
            _operationState.value = OperationState.Loading

            authRepository.updateNickname(nickname)
                .onSuccess { user ->
                    _uiState.value = SettingsUiState.Success(user)
                    _operationState.value = OperationState.Success("昵称更新成功")
                    logSuccess("昵称更新成功")
                }
                .onFailure { error ->
                    _operationState.value = OperationState.Error(error.message ?: "更新失败")
                    logError("昵称更新失败", error as? Exception)
                }

            logMethodEnd("更新昵称")
        }
    }

    /**
     * 修改密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    fun changePassword(oldPassword: String, newPassword: String) {
        logMethodStart("修改密码")

        safeLaunch("修改密码", onError = { _operationState.value = OperationState.Error(it.message ?: "修改失败") }) {
            _operationState.value = OperationState.Loading

            authRepository.changePassword(oldPassword, newPassword)
                .onSuccess {
                    _operationState.value = OperationState.Success("密码修改成功")
                    logSuccess("密码修改成功")
                }
                .onFailure { error ->
                    _operationState.value = OperationState.Error(error.message ?: "修改失败")
                    logError("密码修改失败", error as? Exception)
                }

            logMethodEnd("修改密码")
        }
    }

    /**
     * 更新头像
     * @param avatar 头像 URL
     */
    fun updateAvatar(avatar: String) {
        logMethodStart("更新头像")
        logD("新头像: $avatar")

        safeLaunch("更新头像", onError = { _operationState.value = OperationState.Error(it.message ?: "更新失败") }) {
            _operationState.value = OperationState.Loading

            authRepository.updateAvatar(avatar)
                .onSuccess { user ->
                    _uiState.value = SettingsUiState.Success(user)
                    _operationState.value = OperationState.Success("头像更新成功")
                    logSuccess("头像更新成功")
                }
                .onFailure { error ->
                    _operationState.value = OperationState.Error(error.message ?: "更新失败")
                    logError("头像更新失败", error as? Exception)
                }

            logMethodEnd("更新头像")
        }
    }

    /**
     * 更新主题设置
     * @param theme 主题设置（light/dark/auto）
     */
    fun updateTheme(theme: String) {
        logMethodStart("更新主题设置")
        logD("新主题: $theme")

        safeLaunch("更新主题设置", onError = { _operationState.value = OperationState.Error(it.message ?: "更新失败") }) {
            _operationState.value = OperationState.Loading

            authRepository.updateTheme(theme)
                .onSuccess { user ->
                    _uiState.value = SettingsUiState.Success(user)
                    _operationState.value = OperationState.Success("主题设置已更新")
                    logSuccess("主题设置更新成功")
                }
                .onFailure { error ->
                    _operationState.value = OperationState.Error(error.message ?: "更新失败")
                    logError("主题设置更新失败", error as? Exception)
                }

            logMethodEnd("更新主题设置")
        }
    }

    /**
     * 重置操作状态
     */
    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }

    /**
     * 用户登出
     */
    fun logout(onLogoutSuccess: () -> Unit) {
        logMethodStart("登出")
        viewModelScope.launch {
            authRepository.logout()
            onLogoutSuccess()
            logSuccess("登出成功")
            logMethodEnd("登出")
        }
    }
}

/**
 * 设置页面 UI 状态
 */
sealed class SettingsUiState {
    data object Loading : SettingsUiState()
    data object NotLoggedIn : SettingsUiState()
    data class Success(val user: User) : SettingsUiState()
}

/**
 * 操作状态
 */
sealed class OperationState {
    data object Idle : OperationState()
    data object Loading : OperationState()
    data class Success(val message: String) : OperationState()
    data class Error(val message: String) : OperationState()
}