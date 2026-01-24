package com.example.babyfood.presentation.ui.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.strategy.StrategyManager
import com.example.babyfood.data.strategy.StrategyType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiSettingsViewModel @Inject constructor(
    private val strategyManager: StrategyManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiSettingsUiState())
    val uiState: StateFlow<AiSettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val currentStrategy = strategyManager.getCurrentAiStrategy()
            _uiState.value = _uiState.value.copy(
                strategyType = currentStrategy,
                isAiEnabled = currentStrategy != StrategyType.DISABLED
            )
        }
    }

    fun setStrategyType(strategyType: StrategyType) {
        viewModelScope.launch {
            strategyManager.setAiStrategy(strategyType)
            _uiState.value = _uiState.value.copy(
                strategyType = strategyType,
                isAiEnabled = strategyType != StrategyType.DISABLED
            )
        }
    }

    fun enableRemoteAi() {
        viewModelScope.launch {
            strategyManager.enableRemoteAi()
            _uiState.value = _uiState.value.copy(
                strategyType = strategyManager.getCurrentAiStrategy(),
                isAiEnabled = true
            )
        }
    }

    fun disableRemoteAi() {
        viewModelScope.launch {
            strategyManager.disableRemoteAi()
            _uiState.value = _uiState.value.copy(
                strategyType = strategyManager.getCurrentAiStrategy(),
                isAiEnabled = true
            )
        }
    }

    fun toggleAiEnabled(enabled: Boolean) {
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
        }
    }
}

data class AiSettingsUiState(
    val strategyType: StrategyType = StrategyType.LOCAL,
    val isAiEnabled: Boolean = true
)