package com.example.babyfood.data.strategy

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 策略类型枚举
 */
enum class StrategyType {
    /**
     * 本地策略：使用本地实现
     */
    LOCAL,

    /**
     * 远程策略：使用远程实现
     */
    REMOTE,

    /**
     * 混合策略：优先远程，失败时降级到本地
     */
    HYBRID,

    /**
     * 禁用策略：不使用任何策略
     */
    DISABLED
}

/**
 * 策略管理器
 * 统一管理本地/远程策略切换，支持运行时动态切换
 * 用于云存储和 AI 服务的策略管理
 */
@Singleton
class StrategyManager @Inject constructor() {

    /**
     * 当前同步策略
     * 默认为 LOCAL（仅本地存储）
     */
    private val _syncStrategy = MutableStateFlow(StrategyType.LOCAL)
    val syncStrategy: Flow<StrategyType> = _syncStrategy.asStateFlow()

    /**
     * 当前 AI 分析策略
     * 默认为 LOCAL（本地规则引擎）
     */
    private val _aiStrategy = MutableStateFlow(StrategyType.LOCAL)
    val aiStrategy: Flow<StrategyType> = _aiStrategy.asStateFlow()

    /**
     * 是否启用云同步
     */
    private val _isCloudSyncEnabled = MutableStateFlow(false)
    val isCloudSyncEnabled: Flow<Boolean> = _isCloudSyncEnabled.asStateFlow()

    /**
     * 是否启用远程 AI 分析
     */
    private val _isRemoteAiEnabled = MutableStateFlow(false)
    val isRemoteAiEnabled: Flow<Boolean> = _isRemoteAiEnabled.asStateFlow()

    /**
     * 获取当前同步策略
     */
    fun getCurrentSyncStrategy(): StrategyType = _syncStrategy.value

    /**
     * 获取当前 AI 策略
     */
    fun getCurrentAiStrategy(): StrategyType = _aiStrategy.value

    /**
     * 设置同步策略
     * @param strategy 策略类型
     */
    fun setSyncStrategy(strategy: StrategyType) {
        _syncStrategy.value = strategy
        _isCloudSyncEnabled.value = strategy != StrategyType.LOCAL
    }

    /**
     * 设置 AI 策略
     * @param strategy 策略类型
     */
    fun setAiStrategy(strategy: StrategyType) {
        _aiStrategy.value = strategy
        _isRemoteAiEnabled.value = strategy != StrategyType.LOCAL
    }

    /**
     * 启用云同步
     * 将同步策略设置为混合模式（优先远程，失败时降级到本地）
     */
    fun enableCloudSync() {
        setSyncStrategy(StrategyType.HYBRID)
    }

    /**
     * 禁用云同步
     * 将同步策略设置为本地模式
     */
    fun disableCloudSync() {
        setSyncStrategy(StrategyType.LOCAL)
    }

    /**
     * 启用远程 AI 分析
     * 将 AI 策略设置为混合模式（优先远程，失败时降级到本地）
     */
    fun enableRemoteAi() {
        setAiStrategy(StrategyType.HYBRID)
    }

    /**
     * 禁用远程 AI 分析
     * 将 AI 策略设置为本地模式
     */
    fun disableRemoteAi() {
        setAiStrategy(StrategyType.LOCAL)
    }

    /**
     * 重置所有策略为本地模式
     * 用于用户登出或网络不可用时
     */
    fun resetToLocal() {
        setSyncStrategy(StrategyType.LOCAL)
        setAiStrategy(StrategyType.LOCAL)
    }
}

/**
 * 策略执行结果
 */
sealed class StrategyResult<out T> {
    /**
     * 成功
     */
    data class Success<T>(val data: T) : StrategyResult<T>()

    /**
     * 失败
     */
    data class Failure(val error: Throwable) : StrategyResult<Nothing>()

    /**
     * 降级（远程失败，使用本地结果）
     */
    data class Fallback<T>(val data: T, val error: Throwable) : StrategyResult<T>()
}

/**
 * 策略执行器
 * 辅助类，用于执行策略并处理降级逻辑
 */
class StrategyExecutor<T>(
    private val strategyType: StrategyType,
    private val localStrategy: suspend () -> T,
    private val remoteStrategy: suspend () -> T
) {
    /**
     * 执行策略
     */
    suspend fun execute(): StrategyResult<T> {
        return when (strategyType) {
            StrategyType.LOCAL -> {
                try {
                    val result = localStrategy()
                    StrategyResult.Success(result)
                } catch (e: Exception) {
                    StrategyResult.Failure(e)
                }
            }

            StrategyType.REMOTE -> {
                try {
                    val result = remoteStrategy()
                    StrategyResult.Success(result)
                } catch (e: Exception) {
                    StrategyResult.Failure(e)
                }
            }

            StrategyType.HYBRID -> {
                try {
                    // 先尝试远程
                    val result = remoteStrategy()
                    StrategyResult.Success(result)
                } catch (e: Exception) {
                    // 远程失败，降级到本地
                    try {
                        val localResult = localStrategy()
                        StrategyResult.Fallback(localResult, e)
                    } catch (localError: Exception) {
                        // 本地也失败，返回远程错误
                        StrategyResult.Failure(e)
                    }
                }
            }

            StrategyType.DISABLED -> {
                // 策略已禁用，返回失败
                StrategyResult.Failure(Exception("Strategy is disabled"))
            }
        }
    }
}

/**
 * 创建策略执行器
 */
inline fun <T> createStrategyExecutor(
    strategyType: StrategyType,
    crossinline localStrategy: suspend () -> T,
    crossinline remoteStrategy: suspend () -> T
): StrategyExecutor<T> {
    return StrategyExecutor(
        strategyType = strategyType,
        localStrategy = { localStrategy() },
        remoteStrategy = { remoteStrategy() }
    )
}