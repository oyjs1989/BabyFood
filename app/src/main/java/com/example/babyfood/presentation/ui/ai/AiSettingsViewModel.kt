package com.example.babyfood.presentation.ui.ai

import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.strategy.StrategyManager
import com.example.babyfood.data.strategy.StrategyType
import com.example.babyfood.presentation.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiSettingsViewModel @Inject constructor(
    private val strategyManager: StrategyManager
) : BaseViewModel() {

    override val logTag: String = "AiSettingsViewModel"

    private val _uiState = MutableStateFlow(AiSettingsUiState())
    val uiState: StateFlow<AiSettingsUiState> = _uiState.asStateFlow()

    init {
        logMethodStart("AiSettingsViewModel 初始化")
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val currentStrategy = strategyManager.getCurrentAiStrategy()
            _uiState.value = _uiState.value.copy(
                strategyType = currentStrategy,
                isAiEnabled = currentStrategy != StrategyType.DISABLED
            )
            logD("加载设置: strategy=$currentStrategy, enabled=${currentStrategy != StrategyType.DISABLED}")
        }
    }

    fun setStrategyType(strategyType: StrategyType) {
        logD("设置策略类型: $strategyType")
        viewModelScope.launch {
            strategyManager.setAiStrategy(strategyType)
            _uiState.value = _uiState.value.copy(
                strategyType = strategyType,
                isAiEnabled = strategyType != StrategyType.DISABLED
            )
            logSuccess("策略类型已更新: $strategyType")
        }
    }

    fun enableRemoteAi() {
        logMethodStart("启用远程 AI")
        viewModelScope.launch {
            strategyManager.enableRemoteAi()
            _uiState.value = _uiState.value.copy(
                strategyType = strategyManager.getCurrentAiStrategy(),
                isAiEnabled = true
            )
            logSuccess("远程 AI 已启用")
            logMethodEnd("启用远程 AI")
        }
    }

    fun disableRemoteAi() {
        logMethodStart("禁用远程 AI")
        viewModelScope.launch {
            strategyManager.disableRemoteAi()
            _uiState.value = _uiState.value.copy(
                strategyType = strategyManager.getCurrentAiStrategy(),
                isAiEnabled = true
            )
            logSuccess("远程 AI 已禁用")
            logMethodEnd("禁用远程 AI")
        }
    }

    fun toggleAiEnabled(enabled: Boolean) {
        logD("切换 AI 启用状态: $enabled")
        viewModelScope.launch {
            val newStrategy = if (enabled) {
                StrategyType.LOCAL
            } else {
                StrategyType.DISABLED
            }
            strategyManager.setAiStrategy(newStrategy)
            _uiState.value = _uiState.value.copy(
                strategyType = newStrategy,
                isAiEnabled = enabled
            )
            logSuccess("AI 状态已更新: enabled=$enabled, strategy=$newStrategy")
        }
    }
}

data class AiSettingsUiState(
    val strategyType: StrategyType = StrategyType.LOCAL,
    val isAiEnabled: Boolean = true
)
