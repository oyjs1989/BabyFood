package com.example.babyfood.presentation.ui.baby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.preferences.PreferencesManager
import com.example.babyfood.data.repository.BabyRepository
import com.example.babyfood.data.repository.HealthRecordRepository
import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.HealthRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class BabyViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val healthRecordRepository: HealthRecordRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    companion object {
        private const val TAG = "BabyViewModel"
    }

    private val _uiState = MutableStateFlow(BabyUiState())
    val uiState: StateFlow<BabyUiState> = _uiState.asStateFlow()

    init {
        loadBabies()
    }

    private fun loadBabies() {
        Log.d(TAG, "========== 开始加载宝宝列表 ==========")
        viewModelScope.launch {
            babyRepository.getAllBabies().collect { babies ->
                // 加载当前选中的宝宝 ID
                val selectedBabyId = preferencesManager.getSelectedBabyId()
                Log.d(TAG, "当前选中的宝宝 ID: $selectedBabyId")

                _uiState.value = _uiState.value.copy(
                    babies = babies,
                    isLoading = false,
                    selectedBabyId = selectedBabyId
                )
                Log.d(TAG, "✓ 宝宝列表加载完成，共 ${babies.size} 个宝宝 ==========")
            }
        }
    }

    fun saveBaby(baby: Baby) {
        viewModelScope.launch {
            try {
                if (baby.id == 0L) {
                    babyRepository.insertBaby(baby)
                } else {
                    babyRepository.updateBaby(baby)
                }
                _uiState.value = _uiState.value.copy(
                    isSaved = true,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun deleteBaby(baby: Baby) {
        viewModelScope.launch {
            try {
                babyRepository.deleteBaby(baby)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSavedFlag() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }

    fun loadBaby(babyId: Long) {
        viewModelScope.launch {
            val baby = babyRepository.getBabyById(babyId)
            _uiState.value = _uiState.value.copy(selectedBaby = baby)
            loadLatestHealthRecord(babyId)
        }
    }

    fun loadLatestHealthRecord(babyId: Long) {
        viewModelScope.launch {
            val latestRecord = healthRecordRepository.getLatestHealthRecord(babyId)
            _uiState.value = _uiState.value.copy(latestHealthRecord = latestRecord)
        }
    }

    /**
     * 设置宝宝为当前选中的宝宝
     * @param baby 要设置为当前的宝宝
     */
    fun setAsCurrentBaby(baby: Baby) {
        Log.d(TAG, "========== 设置宝宝为当前 ==========")
        Log.d(TAG, "宝宝: ${baby.name} (ID: ${baby.id})")

        // 保存到 SharedPreferences
        preferencesManager.saveSelectedBabyId(baby.id)

        // 更新 UI 状态
        _uiState.value = _uiState.value.copy(selectedBabyId = baby.id)

        Log.d(TAG, "✓ 设置完成 ==========")
    }

    /**
     * 清除当前选中的宝宝
     */
    fun clearCurrentBaby() {
        Log.d(TAG, "========== 清除当前宝宝 ==========")
        preferencesManager.clearSelectedBabyId()
        _uiState.value = _uiState.value.copy(selectedBabyId = -1L)
        Log.d(TAG, "✓ 清除完成 ==========")
    }
}

data class BabyUiState(
    val babies: List<Baby> = emptyList(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val error: String? = null,
    val selectedBaby: Baby? = null,
    val latestHealthRecord: HealthRecord? = null,
    val selectedBabyId: Long = -1L  // 当前选中的宝宝 ID（用于全局状态）
)