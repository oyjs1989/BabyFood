package com.example.babyfood.presentation.ui

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * ViewModel 状态管理扩展函数
 * 简化 StateFlow 的状态更新操作
 */

/**
 * 通用的状态更新函数
 * 使用 lambda 表达式简化状态更新
 */
inline fun <T> MutableStateFlow<T>.updateState(crossinline block: T.() -> T) {
    value = value.block()
}

/**
 * 清除错误信息
 * 假设状态类有 error 字段
 */
inline fun <T> MutableStateFlow<T>.clearError(crossinline block: T.(String?) -> T) {
    value = value.block(null)
}

/**
 * 设置错误信息
 * 假设状态类有 error 字段
 */
inline fun <T> MutableStateFlow<T>.setError(message: String?, crossinline block: T.(String?) -> T) {
    value = value.block(message)
}

/**
 * 设置加载状态
 * 假设状态类有 isLoading 字段
 */
inline fun <T> MutableStateFlow<T>.setLoading(isLoading: Boolean = true, crossinline block: T.(Boolean) -> T) {
    value = value.block(isLoading)
}

/**
 * 设置保存状态
 * 假设状态类有 isSaved 字段
 */
inline fun <T> MutableStateFlow<T>.setSaved(isSaved: Boolean = true, crossinline block: T.(Boolean) -> T) {
    value = value.block(isSaved)
}

/**
 * 设置生成状态
 * 假设状态类有 isGenerating 字段
 */
inline fun <T> MutableStateFlow<T>.setGenerating(isGenerating: Boolean = true, crossinline block: T.(Boolean) -> T) {
    value = value.block(isGenerating)
}

/**
 * 清除错误和加载状态
 * 假设状态类有 error 和 isLoading 字段
 */
inline fun <T> MutableStateFlow<T>.clearErrorAndLoading(
    crossinline block: T.(String?, Boolean) -> T
) {
    value = value.block(null, false)
}

/**
 * 清除错误和保存状态
 * 假设状态类有 error 和 isSaved 字段
 */
inline fun <T> MutableStateFlow<T>.clearErrorAndSaved(
    crossinline block: T.(String?, Boolean) -> T
) {
    value = value.block(null, false)
}