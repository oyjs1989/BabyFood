package com.example.babyfood.presentation.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * BaseViewModel - 所有 ViewModel 的基类
 *
 * 提供通用功能：
 * 1. 统一的日志输出（遵循项目日志规范）
 * 2. 通用的错误处理流程
 * 3. 登录状态检查
 * 4. 安全的协程启动
 */
abstract class BaseViewModel : ViewModel() {

    /**
     * 日志 TAG，子类必须提供
     */
    protected abstract val logTag: String

    // ==================== 日志方法 ====================

    /**
     * 打印调试日志
     */
    protected fun logD(message: String) {
        Log.d(logTag, message)
    }

    /**
     * 打印信息日志
     */
    protected fun logI(message: String) {
        Log.i(logTag, message)
    }

    /**
     * 打印警告日志
     */
    protected fun logW(message: String) {
        Log.w(logTag, message)
    }

    /**
     * 打印错误日志
     */
    protected fun logE(message: String, throwable: Throwable? = null) {
        Log.e(logTag, message, throwable)
    }

    /**
     * 打印方法开始日志
     * 格式：========== 方法名 开始 ==========
     */
    protected fun logMethodStart(methodName: String) {
        Log.d(logTag, "========== $methodName 开始 ==========")
    }

    /**
     * 打印方法结束日志
     * 格式：========== 方法名 完成 ==========
     */
    protected fun logMethodEnd(methodName: String) {
        Log.d(logTag, "========== $methodName 完成 ==========")
    }

    /**
     * 打印操作成功日志
     * 格式：✓ 操作描述
     */
    protected fun logSuccess(message: String) {
        Log.d(logTag, "✓ $message")
    }

    /**
     * 打印操作失败日志
     * 格式：❌ 操作描述: 错误信息
     */
    protected fun logError(message: String, error: Throwable? = null) {
        Log.e(logTag, "❌ $message", error)
    }

    /**
     * 打印警告信息日志
     * 格式：⚠️ 警告信息
     */
    protected fun logWarning(message: String) {
        Log.w(logTag, "⚠️ $message")
    }

    // ==================== 协程工具 ====================

    /**
     * 安全地启动协程，自动捕获异常并记录日志
     *
     * @param block 要执行的挂起函数
     * @param onError 错误处理回调
     * @param errorMessage 错误日志消息
     */
    protected fun safeLaunch(
        errorMessage: String = "操作",
        onError: (Exception) -> Unit = {},
        block: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                logError("$errorMessage 失败", e)
                onError(e)
            }
        }
    }

    /**
     * 安全地启动协程，带返回值
     *
     * @param block 要执行的挂起函数
     * @param onSuccess 成功回调
     * @param onError 错误处理回调
     * @param errorMessage 错误日志消息
     */
    protected fun <T> safeLaunchWithResult(
        errorMessage: String = "操作",
        onSuccess: (T) -> Unit = {},
        onError: (Exception) -> Unit = {},
        block: suspend () -> T
    ) {
        viewModelScope.launch {
            try {
                val result = block()
                onSuccess(result)
            } catch (e: Exception) {
                logError("$errorMessage 失败", e)
                onError(e)
            }
        }
    }

    // ==================== 登录状态检查 ====================

    /**
     * 检查用户是否已登录
     * @param currentUser 当前用户
     * @param onNotLoggedIn 未登录时的回调
     * @return 是否已登录
     */
    protected fun checkLoggedIn(
        currentUser: User?,
        onNotLoggedIn: () -> Unit = {}
    ): Boolean {
        return if (currentUser == null) {
            logW("用户未登录")
            onNotLoggedIn()
            false
        } else {
            true
        }
    }

    /**
     * 检查用户是否已登录（带日志）
     * @param currentUser 当前用户
     * @param operationName 操作名称（用于日志）
     * @param onNotLoggedIn 未登录时的回调
     * @return 是否已登录
     */
    protected fun checkLoggedInForOperation(
        currentUser: User?,
        operationName: String,
        onNotLoggedIn: () -> Unit = {}
    ): Boolean {
        return if (currentUser == null) {
            logW("用户未登录，无法执行: $operationName")
            onNotLoggedIn()
            false
        } else {
            logD("用户已登录，执行: $operationName")
            true
        }
    }
}

/**
 * 带 UI 状态的 BaseViewModel
 * 适用于有明确 UI 状态的 ViewModel
 *
 * @param T UI 状态类型
 */
abstract class BaseUiViewModel<T : BaseUiState> : BaseViewModel() {

    /**
     * UI 状态 StateFlow
     * 子类需要提供初始状态
     */
    protected abstract val _uiState: MutableStateFlow<T>

    /**
     * 公开的状态只读访问
     */
    val uiState = _uiState

    /**
     * 获取当前状态值
     */
    protected val currentState: T
        get() = _uiState.value

    /**
     * 更新状态
     */
    protected fun updateState(update: T.() -> T) {
        _uiState.value = _uiState.value.update()
    }

    /**
     * 设置加载状态
     */
    protected fun setLoading(isLoading: Boolean = true) {
        _uiState.setLoading(isLoading) { loading ->
            copyWithLoading(loading)
        }
    }

    /**
     * 设置错误信息
     */
    protected fun setError(message: String?) {
        _uiState.setError(message) { error ->
            copyWithError(error)
        }
    }

    /**
     * 清除错误
     */
    protected fun clearError() {
        _uiState.clearError { error ->
            copyWithError(error)
        }
    }

    /**
     * 清除错误和加载状态
     */
    protected fun clearErrorAndLoading() {
        _uiState.clearErrorAndLoading { error, loading ->
            copyWithErrorAndLoading(error, loading)
        }
    }

    /**
     * 子类必须实现：复制状态并修改加载状态
     */
    protected abstract fun T.copyWithLoading(isLoading: Boolean): T

    /**
     * 子类必须实现：复制状态并修改错误信息
     */
    protected abstract fun T.copyWithError(error: String?): T

    /**
     * 子类必须实现：复制状态并修改错误和加载状态
     */
    protected abstract fun T.copyWithErrorAndLoading(error: String?, isLoading: Boolean): T
}

/**
 * 基础 UI 状态接口
 * 所有 UI 状态类应该实现此接口
 */
interface BaseUiState {
    val isLoading: Boolean
    val error: String?
}
