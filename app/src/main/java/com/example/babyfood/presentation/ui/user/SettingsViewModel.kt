package com.example.babyfood.presentation.ui.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.repository.AuthRepository
import com.example.babyfood.domain.model.User
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
) : ViewModel() {

    companion object {
        private const val TAG = "SettingsViewModel"
    }

    // UI 状态
    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    // 操作状态
    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState.asStateFlow()

    init {
        android.util.Log.d(TAG, "========== SettingsViewModel 初始化 ==========")
        loadUserInfo()
    }

    /**
     * 加载用户信息
     */
    fun loadUserInfo() {
        android.util.Log.d(TAG, "========== 开始加载用户信息 ==========")
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                if (user != null) {
                    _uiState.value = SettingsUiState.Success(user)
                    android.util.Log.d(TAG, "✓ 用户信息加载成功")
                    android.util.Log.d(TAG, "用户昵称: ${user.nickname}")
                    android.util.Log.d(TAG, "用户主题: ${user.theme}")
                } else {
                    _uiState.value = SettingsUiState.NotLoggedIn
                    android.util.Log.w(TAG, "⚠️ 用户未登录")
                }
                android.util.Log.d(TAG, "========== 用户信息加载完成 ==========")
            }
        }
    }

    /**
     * 更新昵称
     * @param nickname 新昵称
     */
    fun updateNickname(nickname: String) {
        android.util.Log.d(TAG, "========== 开始更新昵称 ==========")
        android.util.Log.d(TAG, "新昵称: $nickname")

        viewModelScope.launch {
            _operationState.value = OperationState.Loading

            val result = authRepository.updateNickname(nickname)
            result.onSuccess { user ->
                _uiState.value = SettingsUiState.Success(user)
                _operationState.value = OperationState.Success("昵称更新成功")
                android.util.Log.d(TAG, "✓ 昵称更新成功")
            }.onFailure { error ->
                _operationState.value = OperationState.Error(error.message ?: "更新失败")
                android.util.Log.e(TAG, "❌ 昵称更新失败: ${error.message}")
            }

            android.util.Log.d(TAG, "========== 昵称更新完成 ==========")
        }
    }

    /**
     * 修改密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    fun changePassword(oldPassword: String, newPassword: String) {
        android.util.Log.d(TAG, "========== 开始修改密码 ==========")

        viewModelScope.launch {
            _operationState.value = OperationState.Loading

            val result = authRepository.changePassword(oldPassword, newPassword)
            result.onSuccess {
                _operationState.value = OperationState.Success("密码修改成功")
                android.util.Log.d(TAG, "✓ 密码修改成功")
            }.onFailure { error ->
                _operationState.value = OperationState.Error(error.message ?: "修改失败")
                android.util.Log.e(TAG, "❌ 密码修改失败: ${error.message}")
            }

            android.util.Log.d(TAG, "========== 密码修改完成 ==========")
        }
    }

    /**
     * 更新头像
     * @param avatar 头像 URL
     */
    fun updateAvatar(avatar: String) {
        android.util.Log.d(TAG, "========== 开始更新头像 ==========")
        android.util.Log.d(TAG, "新头像: $avatar")

        viewModelScope.launch {
            _operationState.value = OperationState.Loading

            val result = authRepository.updateAvatar(avatar)
            result.onSuccess { user ->
                _uiState.value = SettingsUiState.Success(user)
                _operationState.value = OperationState.Success("头像更新成功")
                android.util.Log.d(TAG, "✓ 头像更新成功")
            }.onFailure { error ->
                _operationState.value = OperationState.Error(error.message ?: "更新失败")
                android.util.Log.e(TAG, "❌ 头像更新失败: ${error.message}")
            }

            android.util.Log.d(TAG, "========== 头像更新完成 ==========")
        }
    }

    /**
     * 更新主题设置
     * @param theme 主题设置（light/dark/auto）
     */
    fun updateTheme(theme: String) {
        android.util.Log.d(TAG, "========== 开始更新主题设置 ==========")
        android.util.Log.d(TAG, "新主题: $theme")

        viewModelScope.launch {
            _operationState.value = OperationState.Loading

            val result = authRepository.updateTheme(theme)
            result.onSuccess { user ->
                _uiState.value = SettingsUiState.Success(user)
                _operationState.value = OperationState.Success("主题设置已更新")
                android.util.Log.d(TAG, "✓ 主题设置更新成功")
            }.onFailure { error ->
                _operationState.value = OperationState.Error(error.message ?: "更新失败")
                android.util.Log.e(TAG, "❌ 主题设置更新失败: ${error.message}")
            }

            android.util.Log.d(TAG, "========== 主题设置更新完成 ==========")
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
        android.util.Log.d(TAG, "========== 开始登出 ==========")
        viewModelScope.launch {
            authRepository.logout()
            onLogoutSuccess()
            android.util.Log.d(TAG, "✓ 登出成功")
            android.util.Log.d(TAG, "========== 登出完成 ==========")
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